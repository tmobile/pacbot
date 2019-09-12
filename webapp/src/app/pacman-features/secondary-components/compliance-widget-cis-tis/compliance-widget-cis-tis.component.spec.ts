import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ComplianceWidgetCisTisComponent } from './compliance-widget-cis-tis.component';

describe('ComplianceWidgetCisTisComponent', () => {
  let component: ComplianceWidgetCisTisComponent;
  let fixture: ComponentFixture<ComplianceWidgetCisTisComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ComplianceWidgetCisTisComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ComplianceWidgetCisTisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
