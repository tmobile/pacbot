import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PolicyViolationsListComponent } from './policy-violations-list.component';

describe('PolicyViolationsListComponent', () => {
  let component: PolicyViolationsListComponent;
  let fixture: ComponentFixture<PolicyViolationsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PolicyViolationsListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PolicyViolationsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
