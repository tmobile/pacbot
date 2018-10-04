import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PluginManagementDetailsComponent } from './plugin-management-details.component';

describe('PluginManagementDetailsComponent', () => {
  let component: PluginManagementDetailsComponent;
  let fixture: ComponentFixture<PluginManagementDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PluginManagementDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PluginManagementDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
