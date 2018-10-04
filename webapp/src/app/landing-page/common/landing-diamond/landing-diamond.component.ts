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

import {Component, OnInit, Input, OnChanges} from '@angular/core';
import {UtilsService} from '../../../shared/services/utils.service';
import {glowPulse, fadeInOutDiamond, diamondTitleActive, topGlowTravellerDuration} from './../animations/animations';

@Component({
    selector: 'app-landing-diamond',
    templateUrl: './landing-diamond.component.html',
    styleUrls: ['./landing-diamond.component.css'],
    providers: [UtilsService],
    animations: [glowPulse, fadeInOutDiamond, diamondTitleActive]
})
export class LandingDiamondComponent implements OnInit, OnChanges {
    public index;
    public interval = 2000;
    public pulsingState = 'active-small';
    public topGlowTraveller = false;
    public showText = 'hide';
    public centerText;
    public pulsingInterval;
    public sequence: any = [
        this.sequenceElement('left', 'ASSESS', 'Continuous compliance assessment'),
        this.sequenceElement('center', 'REPORT', 'Live reports and dashboards'),
        this.sequenceElement('right', 'REMEDIATE', 'Recommendations, one-click & auto fixes')
    ];
    public promises;
    public sequenceEnded = true;
    @Input() inView = false;

    constructor(private utils: UtilsService) {
    }

    ngOnInit() {
        this.pulsingInterval = this.activePulsateLoop();
    }

    ngOnChanges(changes) {
        if (changes.inView.currentValue) {
            this.beginAnimation();
        } else {
            this.stopAnimation();
        }

    }

    nextInSequence(previousIndex, nextIndex) {
        const previousPulseBall = this.sequence[previousIndex];
        this.index = nextIndex;
        const nextPulseBall = this.sequence[nextIndex];
        return this.utils.setTimeoutPromise(0)
            .then(() => {
                previousPulseBall.glowTravellerState = 'end';
                this.showText = 'hide';
                this.togglePulseBall(previousPulseBall, false);

                return this.utils.setTimeoutPromise(500);
            })
            .then(() => {
                this.centerText = nextPulseBall.text;
                this.showText = 'show';
                this.togglePulseBall(nextPulseBall, true);
                return this.utils.setTimeoutPromise(500);
            })
            .then(() => {
                previousPulseBall.glowTravellerState = 'begin';
            });
    }

    activateTopGlowTraveller() {
        const previousPulseBall = this.sequence[2];
        this.index = 0;
        const nextPulseBall = this.sequence[0];
        return this.utils.setTimeoutPromise(0)
            .then(() => {
                this.topGlowTraveller = true;
                this.showText = 'hide';
                this.togglePulseBall(previousPulseBall, false);

                return this.utils.setTimeoutPromise(topGlowTravellerDuration * 2);
            })
            .then(() => {
                this.centerText = nextPulseBall.text;
                this.showText = 'show';
                this.togglePulseBall(nextPulseBall, true);
                return this.utils.setTimeoutPromise(topGlowTravellerDuration * 2);
            })
            .then(() => {
                this.topGlowTraveller = false;
            });
    }

    nextPromise() {
        if (this.inView && this.promises) {
            this.promises.splice(0, 1);
            if (!this.promises.length) {
                this.promises = this.getPromises();
            }
            return this.promises[0]();
        } else if (this.inView && !this.promises) {
            this.stopAnimation();
            this.sequenceEnded = true;
            this.utils.setTimeoutPromise(this.interval)
                .then(() => {
                    this.beginAnimation();
                });
        } else {
            this.stopAnimation();
            this.sequenceEnded = true;
        }
    }

    activePulsateLoop() {
        return setInterval(() => {
            this.togglePulsate();
        }, 1200);
    }

    beginAnimation() {
        if (this.sequenceEnded) {
            this.sequenceEnded = false;
            this.promises = this.getPromises();
            this.index = 0;
            const startingPulseBall = this.sequence[this.index];
            this.togglePulseBall(startingPulseBall, true);
            this.centerText = startingPulseBall.text;
            this.showText = 'show';
            this.promises[0]();
        }
    }

    stopAnimation() {
        this.promises = null;
        for (let i = 0 ; i < this.sequence.length; i++ ) {
            const activePulseBall = this.sequence[i];
            this.togglePulseBall(activePulseBall, false);
        }
        this.showText = 'hide';
    }

    sequenceElement(position, title, text) {
        return {
            position: position,
            text: text,
            title: title,
            active: 'inactive',
            pulseState: this.getPulseState(false),
            glowTravellerState: 'begin'
        };
    }

    togglePulsate() {
        this.pulsingState === 'active-large' ? this.pulsingState = 'active-small' : this.pulsingState = 'active-large';
    }

    togglePulseBall(pulseBall, active) {
        pulseBall.active = active ? 'active' : 'inactive';
        pulseBall.pulseState = this.getPulseState(active);
    }

    getPulseState(active) {
        if (active) {
            return () => {
                return this.pulsingState;
            };
        } else {
            return () => {
                return 'default';
            };
        }
    }

    getPromises() {
        return [
            () => {
                return this.utils.setTimeoutPromise(this.interval).then(() => {
                    this.nextPromise();
                });
            },
            () => {
                return this.nextInSequence(0, 1).then(() => {
                    return this.nextPromise();
                });
            },
            () => {
                return this.utils.setTimeoutPromise(this.interval).then(() => {
                    this.nextPromise();
                });
            },
            () => {
                return this.nextInSequence(1, 2).then(() => {
                    return this.nextPromise();
                });
            },
            () => {
                return this.utils.setTimeoutPromise(this.interval).then(() => {
                    this.nextPromise();
                });
            },
            () => {
                return this.activateTopGlowTraveller().then(() => {
                    return this.nextPromise();
                });
            }
        ];
    }
}
