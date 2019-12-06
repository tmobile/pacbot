
import { _throw as observableThrowError } from 'rxjs/observable/throw';
import { Observable } from 'rxjs/Rx';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import {take, filter, catchError, switchMap, finalize} from 'rxjs/operators';
import { Injectable, Injector } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpRequest, HttpHandler, HttpSentEvent, HttpHeaderResponse, HttpProgressEvent, HttpResponse, HttpUserEvent, HttpErrorResponse } from '@angular/common/http';

import { AuthService } from './auth.service';
import { LoggerService } from '../../shared/services/logger.service';

@Injectable()
export class RequestInterceptorService implements HttpInterceptor {

    isRefreshingToken = false;
    tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);

    constructor(private injector: Injector, private loggerService: LoggerService) {}

    addToken(req: HttpRequest<any>, token: string): HttpRequest<any> {
        return req.clone({ setHeaders: { Authorization: 'Bearer ' + token }});
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any> | HttpSentEvent | HttpHeaderResponse | HttpProgressEvent | HttpResponse<any> | HttpUserEvent<any>> {

        const authService = this.injector.get(AuthService);
        if (req.url.includes('user/authorize') || req.url.includes('user/login') || req.url.includes('refreshtoken')) {
            this.loggerService.log('info', 'Not adding the access token for this api - ' + req.url);
            return next.handle(req);
        } else if (req.url.includes('user/logout-session')) {
            this.loggerService.log('info', 'Do not retry when logging user out - ' + req.url);
            return next.handle(this.addToken(req, authService.getAuthToken()));
        }
        return next.handle(this.addToken(req, authService.getAuthToken())).pipe(
            catchError(error => {

                // We don't want to refresh token for some requests like login or refresh token itself
                // So we verify url and we throw an error if it's the case
                if (
                    req.url.includes('refreshtoken') ||
                    req.url.includes('user/login') || req.url.includes('user/authorize')
                ) {
                    // We do another check to see if refresh token failed
                    // In this case we want to logout user and to redirect it to login page
                    if (req.url.includes('user/authorize') || req.url.includes('refreshtoken')) {
                        authService.doLogout();
                    }

                    return Observable.throw(error);
                }

                if (error instanceof HttpErrorResponse) {
                    switch ((<HttpErrorResponse>error).status) {
                        case 400:
                            return this.handle400Error(error);
                        case 401:
                            return this.handle401Error(req, next);
                        default:
                            return observableThrowError(error);
                    }
                } else {
                    return observableThrowError(error);
                }
            })).retry(3);
    }

    handle400Error(error) {
        if (error && error.status === 400 && error.error && error.error.error === 'invalid_grant') {
            // If we get a 400 and the error message is 'invalid_grant', the token is no longer valid so logout.
            return this.logoutUser();
        }

        return observableThrowError(error);
    }

    handle401Error(req: HttpRequest<any>, next: HttpHandler) {
        const authService = this.injector.get(AuthService);

        if (!this.isRefreshingToken) {
            this.isRefreshingToken = true;

            // Reset here so that the following requests wait until the token
            // comes back from the refreshToken call.
            this.tokenSubject.next(null);


            return authService.refreshToken().pipe(
                switchMap((newToken: string) => {
                    if (newToken) {
                        this.tokenSubject.next(newToken);
                        return next.handle(this.addToken(req, newToken));
                    }

                    // If we don't get a new token, we are in trouble so logout.
                    return this.logoutUser();
                }),
                catchError(error => {
                    // If there is an exception calling 'refreshToken', bad news so logout.
                    return this.logoutUser();
                }),
                finalize(() => {
                    this.isRefreshingToken = false;
                })
            );
        } else {
            return this.tokenSubject.pipe(
                filter(token => token != null),
                take(1),
                switchMap((token: string) => {
                    return next.handle(this.addToken(req, token));
                }), );
        }
    }

    logoutUser() {
        const authService = this.injector.get(AuthService);
        // Route to the login page (implementation up to you)
        authService.doLogout();
        return observableThrowError('');
    }
}