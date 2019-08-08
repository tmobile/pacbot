import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CopyElementComponent } from './copy-element.component';

describe('CopyElementComponent', () => {
  let component: CopyElementComponent;
  let fixture: ComponentFixture<CopyElementComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CopyElementComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyElementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
