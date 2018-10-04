import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountManagementDetailsComponent } from './account-management-details.component';

describe('AccountManagementDetailsComponent', () => {
  let component: AccountManagementDetailsComponent;
  let fixture: ComponentFixture<AccountManagementDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountManagementDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountManagementDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
