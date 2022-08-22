import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TenderiDetailComponent } from './tenderi-detail.component';

describe('Tenderi Management Detail Component', () => {
  let comp: TenderiDetailComponent;
  let fixture: ComponentFixture<TenderiDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TenderiDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tenderi: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TenderiDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TenderiDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tenderi on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tenderi).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
