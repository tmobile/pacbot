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

import {trigger, state, animate, style, transition, query } from '@angular/animations';

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

export const changeUnderlineColor =
    trigger('changeUnderlineColor', [
        state('pink', style({
            'border-color': '#e20074'
        })),
        transition('* <=> pink', animate('400ms ease-in-out'))
    ]);

export const changeTextColor =
    trigger('changeTextColor', [
        state('pink', style({
            color: '#e20074'
        })),
        transition('* <=> pink', animate('400ms ease-in-out'))
    ]);

// fade.animation.ts
export const fadeAnimation =

    trigger('fadeAnimation', [

        state('void', style({position: 'fixed', opacity: 0 })),
        state('*', style({position: 'relative', opacity: 1 })),

        transition( '* => *', [
            query(':enter, :leave',
                style({position: 'absolute', width: '100%', height: '100%'}), {optional: true}),

            query(':enter',
                [
                    style({ opacity: 0 })
                ],
                { optional: true }
            ),

            query(':leave',
                [
                    style({ opacity: 1 }),
                    animate('.3s', style({ opacity: 0 }))
                ],
                { optional: true }
            ),

            query(':enter',
                [
                    style({ opacity: 0  }),
                    animate('.3s', style({ opacity: 1 }))
                ],
                { optional: true }
            )

        ])

    ]);
