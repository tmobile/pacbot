import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment.stg';
import { Subscription } from 'rxjs/Subscription';
import { AssetGroupObservableService } from '../../../core/services/asset-group-observable.service';
import { DomainTypeObservableService } from '../../../core/services/domain-type-observable.service';
import { ErrorHandlingService } from '../../../shared/services/error-handling.service';
import { AutorefreshService } from '../../services/autorefresh.service';
import { LoggerService } from '../../../shared/services/logger.service';
import { CommonResponseService } from '../../../shared/services/common-response.service';

@Component({
    selector: 'app-compliance-widget-cis-tis',
    templateUrl: './compliance-widget-cis-tis.component.html',
    styleUrls: ['./compliance-widget-cis-tis.component.css'],
    providers: [AutorefreshService]
})
export class ComplianceWidgetCisTisComponent implements OnInit, OnDestroy {
    cisScore;
    subscriptionToAssetGroup: Subscription;
    subscriptionDomain: Subscription;
    dataSubscriber: Subscription;
    selectedAssetGroup: string;
    selectedDomain: any;
    errorMessage;
    durationParams;
    autoRefresh;
    autorefreshInterval;
    apiStatus = 0;

    constructor(
        private assetGroupObservableService: AssetGroupObservableService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private domainObservableService: DomainTypeObservableService,
        private autorefreshService: AutorefreshService,
        private errorHandling: ErrorHandlingService,
        private logger: LoggerService,
        private commonResponseService: CommonResponseService
    ) {
        this.initializeSubscriptions();
    }

    getCisScores() {
        this.apiStatus = 0;
        if (this.dataSubscriber) { this.dataSubscriber.unsubscribe(); }

        const queryParams = {
            'ag': this.selectedAssetGroup,
            'type': 'all'
        };

        const cisScoresUrl = environment.cisScores.url;
        const cisScoresMethod = environment.cisScores.method;

        try {
            this.dataSubscriber = this.commonResponseService.getData(cisScoresUrl, cisScoresMethod, {}, queryParams).subscribe(
                response => {
                    try {
                        if (!response.details.length) {
                            this.apiStatus = -1;
                            this.errorMessage = 'noDataAvailable';
                            return;
                        }
                        this.cisScore = response.score;
                        this.apiStatus = 1;
                    } catch (e) {
                        this.errorMessage = 'jsError';
                        this.apiStatus = -1;
                        this.logger.log('error', e);
                    }
                },
                error => {
                    this.errorMessage = error;
                    this.apiStatus = -1;
                });
        } catch (error) {
            this.errorMessage = 'jsError';
            this.apiStatus = -1;
            this.logger.log('error', error);
        }
    }

    /* subscribe to the asset group and domains mandatory to get data */
    initializeSubscriptions() {
        this.subscriptionToAssetGroup = this.assetGroupObservableService.getAssetGroup().subscribe(assetGroupName => {
            this.selectedAssetGroup = assetGroupName;
            this.getCisScores();
        });

        this.subscriptionDomain = this.domainObservableService.getDomainType().subscribe(domain => {
            this.selectedDomain = domain;
        });
    }

    navigateToCIS() {
        this.router.navigate(['../cis-compliance'], {
            relativeTo: this.activatedRoute,
            queryParamsHandling: 'merge',
            queryParams: {
            }
        });
    }

    ngOnInit() {
        /* Variables to be set only first time when component is loaded should go here. */
        try {
            this.durationParams = this.autorefreshService.getDuration();
            this.durationParams = parseInt(this.durationParams, 10);
            this.autoRefresh = this.autorefreshService.autoRefresh;
            const afterLoad = this;
            if (this.autoRefresh !== undefined) {
                if ((this.autoRefresh === true) || (this.autoRefresh.toString() === 'true')) {

                    this.autorefreshInterval = setInterval(function () {
                        afterLoad.getCisScores();
                    }, this.durationParams);
                }
            }
        } catch (error) {
            this.errorMessage = this.errorHandling.handleJavascriptError(error);
        }
    }

    ngOnDestroy() {
        try {
            this.subscriptionToAssetGroup.unsubscribe();
            this.subscriptionDomain.unsubscribe();
            this.dataSubscriber.unsubscribe();
            clearInterval(this.autorefreshInterval);
        } catch (error) {
            this.logger.log('info', '--- Error while unsubscribing ---');
        }
    }
}
