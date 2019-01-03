import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PacmanLoaderComponent } from './pacman-loader.component';

describe('PacmanLoaderComponent', () => {
  let component: PacmanLoaderComponent;
  let fixture: ComponentFixture<PacmanLoaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PacmanLoaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PacmanLoaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
