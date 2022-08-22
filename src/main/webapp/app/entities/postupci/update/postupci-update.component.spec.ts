import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PostupciService } from '../service/postupci.service';
import { IPostupci, Postupci } from '../postupci.model';

import { PostupciUpdateComponent } from './postupci-update.component';

describe('Postupci Management Update Component', () => {
  let comp: PostupciUpdateComponent;
  let fixture: ComponentFixture<PostupciUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let postupciService: PostupciService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PostupciUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PostupciUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PostupciUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    postupciService = TestBed.inject(PostupciService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const postupci: IPostupci = { id: 456 };

      activatedRoute.data = of({ postupci });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(postupci));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Postupci>>();
      const postupci = { id: 123 };
      jest.spyOn(postupciService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postupci });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postupci }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(postupciService.update).toHaveBeenCalledWith(postupci);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Postupci>>();
      const postupci = new Postupci();
      jest.spyOn(postupciService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postupci });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: postupci }));
      saveSubject.complete();

      // THEN
      expect(postupciService.create).toHaveBeenCalledWith(postupci);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Postupci>>();
      const postupci = { id: 123 };
      jest.spyOn(postupciService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ postupci });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(postupciService.update).toHaveBeenCalledWith(postupci);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
