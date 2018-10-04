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

import {trigger, state, animate, style, transition, query, group, keyframes} from '@angular/animations';

export const loginRouterTransition = trigger('loginRouterTransition', [
    state('in', style({
        'width': '38em'
    })),
    state('out', style({
        'width': '0em'
    })),
    transition('in <=> out', animate('200ms ease-in-out'))
]);


export const changeTextColor =
    trigger('changeTextColor', [
        state('pink', style({
            color: '#e20074'
        })),
        transition('* <=> pink', animate('400ms ease-in-out'))
    ]);

export const changeUnderlineColor =
    trigger('changeUnderlineColor', [
        state('pink', style({
            'border-color': '#e20074'
        })),
        transition('* <=> pink', animate('400ms ease-in-out'))
    ]);

export const fadeInOut =
    trigger('fadeInOut', [
        state('show', style({
            opacity: '1',
        })),
        state('hide', style({
            opacity: '0'
        })),
        transition('show <=> hide', animate('200ms ease-in-out'))
    ]);

export const fadeInOutDiamond =
    trigger('fadeInOutDiamond', [
        state('show', style({
            opacity: '1',
        })),
        state('hide', style({
            opacity: '0'
        })),
        transition('show <=> hide', animate('500ms ease-in-out'))
    ]);


export const glowPulse = trigger('glowPulse', [
    state('active-small', glowPulseStateColor('#F5006D', 2)),
    state('active-large', glowPulseStateColor('#F5006D', 1)),
    state('default', style({
        'background-color': '#6A8DA8'
    })),
    transition('active-large <=> active-small', animate('1200ms ease-in-out')),
    transition('* => default', animate('500ms ease-in-out')),
    transition('default => active-large, default => active-small', animate('500ms ease-in-out'))
]);

function glowPulseStateColor(colorCode, sizeMultiplier) {
    return style({
        'background-color': colorCode,
        'box-shadow':
        '0 0 ' + (5 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (10 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (15 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (20 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (40 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (60 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (75 * sizeMultiplier) + 'px ' + colorCode + ',' +
        '0 0 ' + (5 * sizeMultiplier) + 'px ' + colorCode
    });
}


export const verticalLineTravel = trigger('verticalLineTravel', [
    state('begin', style({
        top: '100%',
        bottom: '0'
    })),
    state('end', style({
        top: '0',
        bottom: '100%'
    })),
    transition('begin => end', [
        animate('1000ms 0s ease-in-out', keyframes([
            style({
                top: '0',
                bottom: '0',
                offset: .5
            }),
            style({
                top: 0,
                bottom: '100%',
                offset: 1
            })
        ]))])
]);

export const horizontalLineTravel = trigger('horizontalLineTravel', [
    state('begin', style({
        left: '0',
        right: '100%'
    })),
    state('end', style({
        left: '100',
        right: '0%'
    })),
    transition('begin => end', [
        animate('1000ms 0s ease-in-out', keyframes([
            style({
                left: '0',
                right: '0',
                offset: .5
            }),
            style({
                left: '100%',
                right: '0',
                offset: 1
            })
        ]))])
]);

export const diamondTitleActive = trigger('diamondTitleActive', [
    state('active', style({
        'opacity': '1',
        'text-shadow': '0 0 .65px #fff, 0 0 .65px #fff'
    })),
    state('inactive', style({
        'opacity': '.7',
    })),
    transition('* <=> *', animate('500ms 0s ease-in-out'))
]);

export const topGlowTravellerDuration = 375;
export const topGlowTraveller = trigger('topGlowTraveller', [
        transition('void => *', [
            query('.glow-traveller-inside-right',
                animate(topGlowTravellerDuration,
                    style({
                        'left': '22em'
                    })
                )
            ),
            query('.glow-traveller-inside-left',
                animate(topGlowTravellerDuration,
                    style({
                        'left': '6em'
                    })
                )
            ),
            query('.glow-traveller-inside-right',
                animate(topGlowTravellerDuration,
                    style({
                        'right': '38em'
                    })
                )
            ),
            query('.glow-traveller-inside-left',
                animate(topGlowTravellerDuration,
                    style({
                        'right': '60em'
                    })
                )
            ),
        ])
    ]);

