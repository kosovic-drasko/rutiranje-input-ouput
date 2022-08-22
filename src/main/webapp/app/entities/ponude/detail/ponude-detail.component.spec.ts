import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PonudeDetailComponent } from './ponude-detail.component';

describe('Ponude Management Detail Component', () => {
  let comp: PonudeDetailComponent;
  let fixture: ComponentFixture<PonudeDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PonudeDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ponude: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PonudeDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PonudeDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ponude on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ponude).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
