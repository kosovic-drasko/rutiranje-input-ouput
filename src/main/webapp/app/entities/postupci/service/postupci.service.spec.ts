import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPostupci, Postupci } from '../postupci.model';

import { PostupciService } from './postupci.service';

describe('Postupci Service', () => {
  let service: PostupciService;
  let httpMock: HttpTestingController;
  let elemDefault: IPostupci;
  let expectedResult: IPostupci | IPostupci[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PostupciService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      sifraPostupka: 0,
      opis: 'AAAAAAA',
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

    it('should create a Postupci', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Postupci()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Postupci', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          sifraPostupka: 1,
          opis: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Postupci', () => {
      const patchObject = Object.assign(
        {
          sifraPostupka: 1,
        },
        new Postupci()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Postupci', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          sifraPostupka: 1,
          opis: 'BBBBBB',
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

    it('should delete a Postupci', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPostupciToCollectionIfMissing', () => {
      it('should add a Postupci to an empty array', () => {
        const postupci: IPostupci = { id: 123 };
        expectedResult = service.addPostupciToCollectionIfMissing([], postupci);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postupci);
      });

      it('should not add a Postupci to an array that contains it', () => {
        const postupci: IPostupci = { id: 123 };
        const postupciCollection: IPostupci[] = [
          {
            ...postupci,
          },
          { id: 456 },
        ];
        expectedResult = service.addPostupciToCollectionIfMissing(postupciCollection, postupci);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Postupci to an array that doesn't contain it", () => {
        const postupci: IPostupci = { id: 123 };
        const postupciCollection: IPostupci[] = [{ id: 456 }];
        expectedResult = service.addPostupciToCollectionIfMissing(postupciCollection, postupci);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postupci);
      });

      it('should add only unique Postupci to an array', () => {
        const postupciArray: IPostupci[] = [{ id: 123 }, { id: 456 }, { id: 78548 }];
        const postupciCollection: IPostupci[] = [{ id: 123 }];
        expectedResult = service.addPostupciToCollectionIfMissing(postupciCollection, ...postupciArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const postupci: IPostupci = { id: 123 };
        const postupci2: IPostupci = { id: 456 };
        expectedResult = service.addPostupciToCollectionIfMissing([], postupci, postupci2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postupci);
        expect(expectedResult).toContain(postupci2);
      });

      it('should accept null and undefined values', () => {
        const postupci: IPostupci = { id: 123 };
        expectedResult = service.addPostupciToCollectionIfMissing([], null, postupci, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postupci);
      });

      it('should return initial array if no Postupci is added', () => {
        const postupciCollection: IPostupci[] = [{ id: 123 }];
        expectedResult = service.addPostupciToCollectionIfMissing(postupciCollection, undefined, null);
        expect(expectedResult).toEqual(postupciCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
