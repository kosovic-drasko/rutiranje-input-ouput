import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PostupciDetailComponent } from './postupci-detail.component';

describe('Postupci Management Detail Component', () => {
  let comp: PostupciDetailComponent;
  let fixture: ComponentFixture<PostupciDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PostupciDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ postupci: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PostupciDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PostupciDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load postupci on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.postupci).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
