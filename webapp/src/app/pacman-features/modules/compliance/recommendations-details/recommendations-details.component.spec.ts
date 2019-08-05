import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendationsDetailsComponent } from './recommendations-details.component';

describe('RecommendationsDetailsComponent', () => {
  let component: RecommendationsDetailsComponent;
  let fixture: ComponentFixture<RecommendationsDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RecommendationsDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RecommendationsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
