import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgGridTableComponent } from './ag-grid-table.component';

describe('AgGridTableComponent', () => {
  let component: AgGridTableComponent;
  let fixture: ComponentFixture<AgGridTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AgGridTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgGridTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
