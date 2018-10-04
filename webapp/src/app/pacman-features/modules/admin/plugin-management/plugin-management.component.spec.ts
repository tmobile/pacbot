import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PluginManagementComponent } from './plugin-management.component';

describe('PluginManagementComponent', () => {
  let component: PluginManagementComponent;
  let fixture: ComponentFixture<PluginManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PluginManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PluginManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
