/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  Component,
  Input,
  ElementRef,
  ViewChild,
  SimpleChanges,
  OnDestroy,
  OnInit,
  OnChanges,
  EventEmitter,
  Output
} from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { LoggerService } from '../../../shared/services/logger.service';
import * as d3 from 'd3-selection';
import * as d3Shape from 'd3-shape';
import * as d3Scale from 'd3-scale';
import * as d3Array from 'd3-array';
import * as d3Axis from 'd3-axis';
import { DatepickerOptions } from 'ng2-datepicker';
import { CommonResponseService } from '../../../shared/services/common-response.service';
import { environment } from './../../../../environments/environment';
import { UtilsService } from '../../../shared/services/utils.service';

@Component({
  selector: 'app-vuln-trend-graph',
  templateUrl: './vuln-trend-graph.component.html',
  styleUrls: ['./vuln-trend-graph.component.css']
})

export class VulnTrendGraphComponent implements OnChanges, OnInit, OnDestroy {

  @Input() graphResponse;
  @Input() axisValues;
  @Input() graphData;
  @Input() svgId;
  @Input() maxVal;
  @Input() months;
  @Input() selectedMonth;
  @Input() parentWidth;
  @Input() adminAccess;
  @Input() infoActive = true;
  @Input() agName;
  @Input() notesData = [];
  @Input() notesResponse;
  @Input() selectedAssetGroup;

  @Output() fetchNewNotes = new EventEmitter();

  agList;
  openNotesModal = false;
  graphX = {};
  graphY = {};
  activeScope;
  editMode = false;
  notesText = '';
  recentData = [];
  currentNoteId;
  dateSelected;
  options: DatepickerOptions  = {
    displayFormat: 'MMM D[,] YYYY',
    maxDate: new Date(),
    minDate: new Date('1970-01-01')
  };

  updateNoteState = 1;

  updateNoteSubscription: Subscription;
  deleteNoteSubscription: Subscription;

  @ViewChild('container') vulnReportGraph: ElementRef;

  constructor(private logger: LoggerService,
              private commonResponseService: CommonResponseService, private utils: UtilsService) { }

  ngOnInit() {
    this.setCurrentScopeAndAgList();
  }

  ngOnChanges(changes: SimpleChanges) {
    try {

      const DataChange = changes['graphData'];
      const widthChange = changes['parentWidth'];
      const agName = changes['agName'];

      if (DataChange && DataChange.currentValue ) {
        this.plotGraph(DataChange.currentValue);
        this.agList = [{id: 'global', text: 'Global'}, { text: this.agName, id: this.selectedAssetGroup}];
        this.activeScope = [{id: this.selectedAssetGroup, text: this.agName}];
      } else if (widthChange && widthChange.currentValue) {
        const currentWidth = widthChange.currentValue;
        const prevWidth = widthChange.previousValue;
        if (currentWidth !== prevWidth && this.parentWidth && this.graphData) {
          setTimeout(() => {
            this.plotGraph(this.graphData);
          }, 10);
        }
      } else if (agName && agName.currentValue) {
          this.agName = agName.currentValue;
          this.setCurrentScopeAndAgList();
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  setCurrentScopeAndAgList() {
    this.agList = [{id: 'global', text: 'Global'}, { text: this.agName, id: this.selectedAssetGroup}];
    this.activeScope = [{id: this.selectedAssetGroup, text: this.agName}];
  }

  plotGraph(data) {

    const newData = data;

    const ele = this.vulnReportGraph.nativeElement;
    if (ele) {
      ele.innerHTML = '';
    }
    const graphThis = this;
    const margin = { top: 45, right: 20, bottom: 20, left: 70 },
    width =
    parseInt(this.parentWidth, 10) -
      margin.left -
      margin.right,
    height =
    200 -
      margin.top -
      margin.bottom;

      const x = d3Scale.scalePoint().range([0, width]);
      x.domain(newData.map(d => d['date']));

      const y0 = d3Scale.scaleLinear().range([height, 0]);

      // Scale the range of the data for each axes
      y0.domain([
        d3Array.min(newData, function(d) {
          return Math.min(
            d['new'],
            d['open'],
            5
          );
        }),
        d3Array.max(newData, function(d) {
          return Math.max(
            d['new'],
            d['open'],
            5
          );
        })
      ]);

      // Area plotting of graph under line

      const area1 = d3Shape.area()
        .x(function(d) { return x(d['date']); })
        .y0(height)
        .y1(function(d) { return y0(d['open']); });

      const area2 = d3Shape.area()
        .x(function(d) { return x(d['date']); })
        .y0(height)
        .y1(function(d) { return y0(d['new']); });


      // Line plot definition

      const valueline1 = d3Shape
        .line()
        .x(function(d) {
          return x(d['date']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['open']);
        });

      const valueline2 = d3Shape
        .line()
        .x(function(d) {
          return x(d['date']);
        })
        .y(function(d) {
          graphThis.findMaxVal(d);
          return y0(d['new']);
        });

      // set dimentions of svg
      const svg = d3
        .select('#' + this.svgId)
        .attr('width', width + margin.left + margin.right)
        .attr('height', height + margin.top + margin.bottom)
        .append('g')
        .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

      // format the data
      newData.forEach(function(d) {
        d['date'] = new Date(d['date']);
        d['open'] = +d['open'];
        d['new'] = +d['new'];
      });

    // Add the X Bottom Axis
    svg
      .append('g')
      .attr('transform', 'translate(0,' + height + ')')
      .attr('class', 'bottomAxis')
      .call(d3Axis.axisBottom(x))
      .call(make_x_gridlines().tickSize(-height).tickValues(x.domain().filter(function(d, i) { return !(i % (graphThis.months[graphThis.selectedMonth] * 4 )); })))
        .selectAll('text')
        .style('text-anchor', 'end')
        .attr('dx', '1.3em')
        .attr('dy', '1.3em')
        .text(function(d, i) { return newData[i * (graphThis.months[graphThis.selectedMonth] * 4 )].dateStr; });

    // Add areas and lines respectively

    svg.append('g')
      .classed('labels-group', true)
      .selectAll('text')
      .data(data)
      .enter()
      .append('text')
      .classed('label', true)
      .text(function(d) { return ''; })
      .style('font-size', '0px')
      .attr('x', function(d, i) {
            graphThis.graphY[d['dateStr']] = y0(d['open']);
            graphThis.graphX[d['dateStr']] = x(d['date']);
            return x(d['date']);
      });

    svg.append('path')
        .data([data])
        .attr('class', 'area')
        .style('clip-path', 'none')
        .attr('d', area1);

    svg.append('path')
       .data([data])
       .attr('class', 'area')
       .style('clip-path', 'none')
       .style('fill', '#f2425f')
       .attr('d', area2);

    svg
      .append('path')
      .data([newData])
      .attr('class', 'line')
      .style('stroke', '#60727f')
      .attr('d', valueline1);

    svg
      .append('path')
      .data([newData])
      .attr('class', 'line')
      .style('stroke', '#f2425f')
      .style('stroke-width', '1px')
      .attr('d', valueline2);

    // Data text with rect as background at each plot point

    svg.append('g')
      .selectAll('rect')
      .data(data)
      .enter()
      .append('rect')
      .filter(function(d, i) { return !(i % (graphThis.months[graphThis.selectedMonth] * 4 )); })
      .attr('fill', '#fff')
      .attr('fill-opacity', '0.6')
      .attr('height', '16px')
      .attr('rx', 3)
      .attr('ry', 3)
      .attr('width', '36px')
      .attr('x', function(d, i) {
        return x(d['date']);
      })
      .attr('y', function(d, i) {
          return y0(d['new']) - y0(d['open']) > 8 ? y0(d['open']) : y0(d['new']) - 8;
      })
      .style('transform', function(d, i) { return i === 0 ? 'translate(2px, -24px)' : 'translate(-18px, -24px)'; });

    svg.append('g')
        .classed('labels-group', true)
        .selectAll('text')
        .data(data)
        .enter()
        .append('text')
        .filter(function(d, i) { return !(i % (graphThis.months[graphThis.selectedMonth] * 4  )); })
        .classed('label', true)
        .text(function(d) { return d['open']; })
        .style('font-size', '9px')
        .style('text-anchor', 'middle')
        .style('fill', '#60727f')
        .style('transform', function(d, i) { return i === 0 ? 'translate(19px, -8px)' : 'translate(0px, -8px)'; })
        .attr('x', function(d, i) {
              return x(d['date']);
        })
        .attr('y', function(d, i) {
              return y0(d['new']) - y0(d['open']) > 8 ? y0(d['open']) : y0(d['new']) - 8;
        })
        .attr('dy', '-5');

    svg.append('g')
        .selectAll('rect')
        .data(data)
        .enter()
        .append('rect')
        .filter(function(d, i) { return !(i % (graphThis.months[graphThis.selectedMonth] * 4  )); })
        .attr('fill', '#fff')
        .attr('fill-opacity', '0.6')
        .attr('height', '16px')
        .attr('rx', 3)
        .attr('ry', 3)
        .attr('width', '36px')
        .attr('x', function(d, i) {
          return x(d['date']);
        })
        .attr('y', function(d, i) {
              return y0(d['new']);
        })
        .style('transform', function(d, i) { return i === 0 ? 'translate(2px, -16px)' : 'translate(-18px, -16px)'; });

    svg.append('g')
        .classed('labels-group', true)
        .selectAll('text')
        .data(data)
        .enter()
        .append('text')
        .filter(function(d, i) { return !(i % (graphThis.months[graphThis.selectedMonth] * 4  )); })
        .classed('label', true)
        .text(function(d) { return d['new']; })
        .style('font-size', '9px')
        .style('fill', '#ed0004')
        .style('transform', function(d, i) { return i === 0 ? 'translateX(19px)' : 'translateX(0px)'; })
        .style('text-anchor', 'middle')
        .attr('x', function(d, i) {
            return x(d['date']);
        })
        .attr('y', function(d, i) {
              return y0(d['new']);
        })
        .attr('dy', '-5');

    // gridlines in x axis function
    function make_x_gridlines() {
        return d3Axis.axisBottom(x).ticks(5);
    }

  }

  findMaxVal(obj) {
    try {
      if (
        !isNaN(obj.open) &&
        !isNaN(obj.new)
      ) {
        this.maxVal[0] = Math.max(
          this.maxVal[0],
          obj['open'],
          obj['new']
        );
        for ( let i = 5; i > 0; i--) {
          this.axisValues['y0'].push(Math.ceil(this.maxVal[0] / 5 * i));
        }
        this.axisValues['y0'] = this.axisValues['y0'].slice(
          this.axisValues['y0'].length - 5
        );
        if (this.axisValues['y0'][0] < 5) {
          this.axisValues['y0'] = [5, 4, 3, 2, 1];
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  graphClicked(data, index, from? ) {
    try {
      if ( this.adminAccess && this.notesResponse > 0 ) {
        let newNote = true;
        this.recentData = [];
        const firstDate = new Date(this.graphData[0].date);
        firstDate.setDate(firstDate.getDate() - 1);
        this.options['minDate'] = firstDate;
        this.options['maxDate'] = this.graphData[this.graphData.length - 1].date;
        this.dateSelected = data.date;
        this.recentData.push({date: this.graphData[index].dateStr, data: this.graphData[index].open, selectedDate: true});
        if ( index !== this.graphData.length - 1) {
          this.recentData.push({date: this.graphData[index + 1].dateStr, data: this.graphData[index + 1].open, selectedDate: false});
        }
        if (index !== 0) {
          this.recentData.unshift({date: this.graphData[index - 1].dateStr, data: this.graphData[index - 1].open, selectedDate: false});
        }
        for ( let i = 0; i < this.notesData.length; i++ ) {
          if ( data.dateStr === this.notesData[i].dateStr ) {
            if (this.notesData[i].data.length === 1 ) {
              newNote = false;
              // edit existing note
              this.openNotesModal = true;
              this.editMode = true;
              if ( data.dateStr === this.notesData[i].dateStr ) {
                this.currentNoteId = this.notesData[i].data[0].noteId;
                this.notesText = this.notesData[i].data[0].note;
                if ( this.notesData[i].data[0].type === 'Global') {
                  this.activeScope = [{id: 'global', text: 'Global'}];
                } else {
                  this.activeScope = [{id: this.selectedAssetGroup, text: this.agName}];
                }
              }
            } else {
              newNote = false;
              if (!from) {
                // handle multiple notes
                this.openNotesModal = false;
                this.notesData[i].vibrate = true;
                const self = this;
                setTimeout(function(){
                  self.notesData[i].vibrate = false;
                }, 400);
              }
              if (from === 'datepicker') {

                for ( let j = 0; j < this.notesData[i].data.length; j++ ) {
                  if ( (this.activeScope[0].id === 'global' && this.notesData[i].data[j].type === 'Global') || ( this.activeScope[0].id !== 'global' && this.notesData[i].data[j].type !== 'Global' )) {
                    this.notesText = this.notesData[i].data[j].note;
                    this.editMode = true;
                  }
                }
              }
            }
            break;
          }
        }
        if (newNote) {
          // handle new note
          this.openNotesModal = true;
          this.notesText = '';
          this.editMode = false;
          this.activeScope = [{id: this.selectedAssetGroup, text: this.agName}];
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  selectAg(tag) {
    this.activeScope[0] = tag;
    let newNote = true;
    for ( let i = 0; i < this.notesData.length; i++) {
      if ( this.dateSelected.toISOString().slice(0, 10) ===  this.notesData[i].date.toISOString().slice(0, 10) ) {
        if (this.notesData[i].data.length === 1) {
          if ( (this.notesData[i].data[0].type === 'Global' && this.activeScope[0].id === 'global') || (this.notesData[i].data[0].type === 'AssetGroup' && this.activeScope[0].id !== 'global') ) {
            this.notesText = this.notesData[i].data[0].note;
            this.editMode = true;
            newNote = false;
          }
        } else {
          for ( let j = 0; j < this.notesData[i].data.length; j++ ) {
            if ( (this.activeScope[0].id === 'global' && this.notesData[i].data[j].type === 'Global') || (this.activeScope[0].id !== 'global' && this.notesData[i].data[j].type === 'AssetGroup') ) {
              this.notesText = this.notesData[i].data[j].note;
              this.editMode = true;
              newNote = false;
            }
          }
        }
        break;
      }
    }

    if ( newNote ) {
      this.notesText = '';
      this.editMode = false;
    }
  }

  noteClicked(data) {
    try {
      if ( this.adminAccess && this.notesResponse > 0 ) {
        this.openNotesModal = true;
        this.editMode = true;
        this.currentNoteId = data.noteId;
        const firstDate = new Date(this.graphData[0].date);
        firstDate.setDate(firstDate.getDate() - 1);
        this.options['minDate'] = firstDate;
        this.options['maxDate'] = this.graphData[this.graphData.length - 1].date;
        this.dateSelected = this.utils.getDateAndTime(data.date); //new Date(data.date);
        this.notesText = data.note;
        for ( let i = 0; i < this.graphData.length; i++) {
          this.recentData = [];
          if (this.graphData[i].dateStr.replace('/', '-') === (data.date).slice(-5)) {
            this.recentData.push({date: this.graphData[i].dateStr, data: this.graphData[i].open, selectedDate: true});
            if ( i !== this.graphData.length - 1) {
              this.recentData.push({date: this.graphData[i + 1].dateStr, data: this.graphData[i + 1].open, selectedDate: false});
            }
            if (i !== 0) {
              this.recentData.unshift({date: this.graphData[i - 1].dateStr, data: this.graphData[i - 1].open, selectedDate: false});
            }
            break;
          }
        }
        if (data.type.toLowerCase() === 'assetgroup') {
          this.activeScope = [{id: this.selectedAssetGroup, text: this.agName}];
        } else {
          this.activeScope = [{id: 'global', text: 'Global'}];
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  getDateData(date) {
    try {
      for (let i = 0; i < this.graphData.length; i++ ) {
        if (date.getDate() === (this.graphData[i].date).getDate() && date.getMonth() === (this.graphData[i].date).getMonth() ) {
          this.graphClicked(this.graphData[i], i, 'datepicker');
          break;
        }
      }
    } catch (e) {
      this.logger.log('error', e);
    }
  }

  capitalizeFirstLetter(string) {
    if (string) {
      return string.charAt(0).toUpperCase() + string.slice(1);
    }
  }

  updateNote() {

    try {

      if (this.notesText.trim() && this.updateNoteState !== 0) {

        this.updateNoteState = 0;

        if (this.deleteNoteSubscription) {
          this.deleteNoteSubscription.unsubscribe();
        }

        let notesPayload = {};

        if (this.activeScope[0].id === 'global') {
          notesPayload = {
            'ag': '',
            'date': this.dateSelected.toISOString(),
            'note': this.capitalizeFirstLetter(this.notesText)
          };
        } else {
          notesPayload = {
            'ag': this.activeScope[0].id,
            'date': this.dateSelected.toISOString(),
            'note': this.capitalizeFirstLetter(this.notesText)
          };
        }

        const updateNoteParam = {};

        const updateNoteUrl = environment.postVulnTrendNotes.url;
        const updateNoteMethod = environment.postVulnTrendNotes.method;
        this.updateNoteSubscription = this.commonResponseService.getData(
          updateNoteUrl, updateNoteMethod, notesPayload, updateNoteParam).subscribe(
          response => {

            try {
              this.openNotesModal = false;
              this.updateNoteState = 1;
              const self = this;
              setTimeout(() => {
                self.fetchNewNotes.emit();
              }, 10);
            } catch (e) {
              this.logger.log('error', e);
              this.updateNoteState = -1;
            }
          },
          error => {
            this.logger.log('error', error);
            this.updateNoteState = -1;
          });
      }
    } catch (e) {
      this.logger.log('error', e);
      this.updateNoteState = -1;
    }
  }

  deleteNote() {

    try {

      if (this.updateNoteState !== 0 ) {

        if (this.updateNoteSubscription) {
          this.updateNoteSubscription.unsubscribe();
        }

        this.updateNoteState = 0;

        const deleteNotePayload = {};
        const deleteNoteParam = {
          noteId: this.currentNoteId
        };

        const deleteNoteUrl = environment.deleteVulnNote.url + '/' + this.currentNoteId;
        const deleteNoteMethod = environment.deleteVulnNote.method;
        this.deleteNoteSubscription = this.commonResponseService.getData(
          deleteNoteUrl, deleteNoteMethod).subscribe(
          response => {

            try {
              this.openNotesModal = false;
              const self = this;
              setTimeout(() => {
                self.fetchNewNotes.emit();
              }, 200);
              this.updateNoteState = 1;
            } catch (e) {
              this.logger.log('error', e);
              this.updateNoteState = -1;
            }
          },
          error => {
            this.logger.log('error', error);
            this.updateNoteState = -1;
          });

        }

    } catch (e) {
      this.logger.log('error', e);
      this.updateNoteState = -1;
    }
  }

  ngOnDestroy() {
    if (this.updateNoteSubscription) {
      this.updateNoteSubscription.unsubscribe();
    }

    if (this.deleteNoteSubscription) {
      this.deleteNoteSubscription.unsubscribe();
    }
  }

}
