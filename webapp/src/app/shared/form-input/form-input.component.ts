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

import {Component, Input, Output, EventEmitter} from '@angular/core';
import {trigger, state, style, transition, animate} from '@angular/animations';
import {changeTextColor, changeUnderlineColor} from './../animations/animations';

@Component({
    selector: 'app-form-input',
    templateUrl: './form-input.component.html',
    styleUrls: ['./form-input.component.css'],
    animations: [
        trigger('labelFocus', [
            state('focused', style({
                'font-size': '.9em',
                'transform': 'translateY(-1.8em)'
            })),
            transition('* <=> focused', animate('400ms ease-in-out')),
        ]),
        trigger('underlineFocus', [
            state('focused', style({
                width: '100%'
            })),
            state('error', style({
                width: '100%',
                'border-color': '#e20074'
            })),
            transition('* <=> focused', animate('400ms ease-in-out')),
            transition('* <=> error', animate('400ms ease-in-out'))

        ]),
        changeTextColor,
        changeUnderlineColor
    ]
})

export class FormInputComponent {
    animationLabelState: string;
    animationUnderlineState: string;
    _throwError: string;
    type: string;

    constructor() {
        this.config = this.config || {};
        this.type = this.config.type || 'text';
    }

    @Input()
    set throwError(throwError) {
        if (throwError) {
            this._throwError = 'pink';
        } else {
            this._throwError = '';
        }
    }

    private _value;
    @Output() valueChange = new EventEmitter();
    @Input()
    set value(value) {
        this._value = value;
        this. valueChange.emit(this._value);
        if (value) {
            this.animationLabelState = 'focused';
        }
    }
    get value() {
        return this._value;
    }

    @Input() inputLabel: string;
    @Input() config: any;
    @Input() autofocus: boolean;
    @Output() onChange = new EventEmitter<string>();
    @Output() onFocusStateChange = new EventEmitter<boolean>();
    @Output() onClickInner = new EventEmitter();

    onChanges($event) {
        this.value = $event;
        this.valueChange.emit(this.value);
    }

    onFocus() {
        this.animationLabelState = 'focused';
        this.animationUnderlineState = 'focused';
        this.onFocusStateChange.emit(true);
    }

    onFocusout() {
        if (!this.value) {
            this.animationLabelState = '';
            this.onFocusStateChange.emit(false);
        }
        this.animationUnderlineState = '';
    }

    onClick() {
        this.onClickInner.emit();
    }
}
