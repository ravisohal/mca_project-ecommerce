import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecommendationsList } from './recommendations-list';

describe('RecommendationsList', () => {
  let component: RecommendationsList;
  let fixture: ComponentFixture<RecommendationsList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendationsList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecommendationsList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
