import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigManagementComponent } from './config-management.component';

describe('ConfigManagementComponent', () => {
  let component: ConfigManagementComponent;
  let fixture: ComponentFixture<ConfigManagementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigManagementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
