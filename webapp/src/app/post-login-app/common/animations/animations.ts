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

import {trigger, state, animate, style, transition, query, group } from '@angular/animations';

export function doTransition1(fromState, toState) {
    return toState > fromState ? true : false;
  }

export function doTransition2(fromState: any, toState: any) {
    return toState < fromState ? true : false;
}

export function moduleTransition() {
    return trigger('mainRouterTransition', [
        state('*', style({position: 'fixed', width: '100%', height: 'calc(100vh - 125px)'})),
        transition(<any> doTransition1, [
                query(':enter, :leave',
                    style({position: 'fixed', width: '100%', height: 'calc(100vh - 150px)', top: '125px'}), {optional: true}),
                query('.contextual-content-container', style({ opacity: 0 })
                , { optional: true }),
                group([
                    query(':enter', [
                        style({opacity: 0}),
                        animate('.5s ease-in-out', style({opacity: 1}))
                    ], {optional: true}),
                    query(':leave', [
                        animate('.5s ease-in-out', style({opacity: '0'}))
                    ], {optional: true})
                ]),
            ]
        ),
        transition(<any> doTransition2, [
                query(':enter, :leave',
                    style({position: 'fixed', width: '100%', height: 'calc(100vh - 150px)', top: '125px'}), {optional: true}),
                query('.contextual-content-container', style({ opacity: 0 })
                    , { optional: true }),
                group([
                    query(':enter', [
                        style({opacity: 0}),
                        animate('.5s ease-in-out', style({opacity: 1}))
                    ], {optional: true}),
                    query(':leave', [
                        animate('.5s ease-in-out', style({opacity: '0'}))
                    ], {optional: true})
                ]),
            ]
        )
    ]);
}

