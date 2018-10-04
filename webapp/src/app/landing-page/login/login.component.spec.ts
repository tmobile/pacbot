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

import { NO_ERRORS_SCHEMA, Class } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { LoginComponent } from './login.component';
import { FormInputComponent } from '../../shared/form-input/form-input.component';
import { ButtonComponent } from '../../shared/button/button.component';
import { OnPremAuthenticationService } from '../../core/services/onprem-authentication.service';
import { UtilsService } from '../../shared/services/utils.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  class MockAuthenticationService {
    public login() {}
    public takeActionAfterLogin() {}
    public formatUsernameWithoutDomain() {}
  }
  class MockUtilsService {
    public isObject() {}
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, NoopAnimationsModule],
      declarations: [LoginComponent, FormInputComponent, ButtonComponent],
      providers: [
        { provide: OnPremAuthenticationService, useClass: MockAuthenticationService },
        { provide: UtilsService, useClass: MockUtilsService }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
