import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoaderMsgComponent } from './loader-msg.component';

describe('LoaderMsgComponent', () => {
  let component: LoaderMsgComponent;
  let fixture: ComponentFixture<LoaderMsgComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoaderMsgComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoaderMsgComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
