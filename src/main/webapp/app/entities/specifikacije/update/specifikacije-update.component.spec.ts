import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpecifikacijeService } from '../service/specifikacije.service';
import { ISpecifikacije, Specifikacije } from '../specifikacije.model';

import { SpecifikacijeUpdateComponent } from './specifikacije-update.component';

describe('Specifikacije Management Update Component', () => {
  let comp: SpecifikacijeUpdateComponent;
  let fixture: ComponentFixture<SpecifikacijeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let specifikacijeService: SpecifikacijeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SpecifikacijeUpdateComponent],
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
      .overrideTemplate(SpecifikacijeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpecifikacijeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    specifikacijeService = TestBed.inject(SpecifikacijeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const specifikacije: ISpecifikacije = { id: 456 };

      activatedRoute.data = of({ specifikacije });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(specifikacije));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Specifikacije>>();
      const specifikacije = { id: 123 };
      jest.spyOn(specifikacijeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ specifikacije });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: specifikacije }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(specifikacijeService.update).toHaveBeenCalledWith(specifikacije);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Specifikacije>>();
      const specifikacije = new Specifikacije();
      jest.spyOn(specifikacijeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ specifikacije });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: specifikacije }));
      saveSubject.complete();

      // THEN
      expect(specifikacijeService.create).toHaveBeenCalledWith(specifikacije);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Specifikacije>>();
      const specifikacije = { id: 123 };
      jest.spyOn(specifikacijeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ specifikacije });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(specifikacijeService.update).toHaveBeenCalledWith(specifikacije);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
