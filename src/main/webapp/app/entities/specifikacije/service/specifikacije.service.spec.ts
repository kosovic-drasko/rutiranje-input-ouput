import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISpecifikacije, Specifikacije } from '../specifikacije.model';

import { SpecifikacijeService } from './specifikacije.service';

describe('Specifikacije Service', () => {
  let service: SpecifikacijeService;
  let httpMock: HttpTestingController;
  let elemDefault: ISpecifikacije;
  let expectedResult: ISpecifikacije | ISpecifikacije[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpecifikacijeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      sifraPostupka: 0,
      naziv: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Specifikacije', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Specifikacije()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Specifikacije', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          sifraPostupka: 1,
          naziv: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Specifikacije', () => {
      const patchObject = Object.assign(
        {
          naziv: 'BBBBBB',
        },
        new Specifikacije()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Specifikacije', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          sifraPostupka: 1,
          naziv: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Specifikacije', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSpecifikacijeToCollectionIfMissing', () => {
      it('should add a Specifikacije to an empty array', () => {
        const specifikacije: ISpecifikacije = { id: 123 };
        expectedResult = service.addSpecifikacijeToCollectionIfMissing([], specifikacije);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(specifikacije);
      });

      it('should not add a Specifikacije to an array that contains it', () => {
        const specifikacije: ISpecifikacije = { id: 123 };
        const specifikacijeCollection: ISpecifikacije[] = [
          {
            ...specifikacije,
          },
          { id: 456 },
        ];
        expectedResult = service.addSpecifikacijeToCollectionIfMissing(specifikacijeCollection, specifikacije);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Specifikacije to an array that doesn't contain it", () => {
        const specifikacije: ISpecifikacije = { id: 123 };
        const specifikacijeCollection: ISpecifikacije[] = [{ id: 456 }];
        expectedResult = service.addSpecifikacijeToCollectionIfMissing(specifikacijeCollection, specifikacije);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(specifikacije);
      });

      it('should add only unique Specifikacije to an array', () => {
        const specifikacijeArray: ISpecifikacije[] = [{ id: 123 }, { id: 456 }, { id: 93417 }];
        const specifikacijeCollection: ISpecifikacije[] = [{ id: 123 }];
        expectedResult = service.addSpecifikacijeToCollectionIfMissing(specifikacijeCollection, ...specifikacijeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const specifikacije: ISpecifikacije = { id: 123 };
        const specifikacije2: ISpecifikacije = { id: 456 };
        expectedResult = service.addSpecifikacijeToCollectionIfMissing([], specifikacije, specifikacije2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(specifikacije);
        expect(expectedResult).toContain(specifikacije2);
      });

      it('should accept null and undefined values', () => {
        const specifikacije: ISpecifikacije = { id: 123 };
        expectedResult = service.addSpecifikacijeToCollectionIfMissing([], null, specifikacije, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(specifikacije);
      });

      it('should return initial array if no Specifikacije is added', () => {
        const specifikacijeCollection: ISpecifikacije[] = [{ id: 123 }];
        expectedResult = service.addSpecifikacijeToCollectionIfMissing(specifikacijeCollection, undefined, null);
        expect(expectedResult).toEqual(specifikacijeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
