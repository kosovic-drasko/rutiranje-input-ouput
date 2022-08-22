import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITenderi } from '../tenderi.model';

import { TenderiService } from './tenderi.service';

describe('Tenderi Service', () => {
  let service: TenderiService;
  let httpMock: HttpTestingController;
  let elemDefault: ITenderi;
  let expectedResult: ITenderi | ITenderi[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TenderiService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
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

    it('should return a list of Tenderi', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
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

    describe('addTenderiToCollectionIfMissing', () => {
      it('should add a Tenderi to an empty array', () => {
        const tenderi: ITenderi = { id: 123 };
        expectedResult = service.addTenderiToCollectionIfMissing([], tenderi);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tenderi);
      });

      it('should not add a Tenderi to an array that contains it', () => {
        const tenderi: ITenderi = { id: 123 };
        const tenderiCollection: ITenderi[] = [
          {
            ...tenderi,
          },
          { id: 456 },
        ];
        expectedResult = service.addTenderiToCollectionIfMissing(tenderiCollection, tenderi);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Tenderi to an array that doesn't contain it", () => {
        const tenderi: ITenderi = { id: 123 };
        const tenderiCollection: ITenderi[] = [{ id: 456 }];
        expectedResult = service.addTenderiToCollectionIfMissing(tenderiCollection, tenderi);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenderi);
      });

      it('should add only unique Tenderi to an array', () => {
        const tenderiArray: ITenderi[] = [{ id: 123 }, { id: 456 }, { id: 4272 }];
        const tenderiCollection: ITenderi[] = [{ id: 123 }];
        expectedResult = service.addTenderiToCollectionIfMissing(tenderiCollection, ...tenderiArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenderi: ITenderi = { id: 123 };
        const tenderi2: ITenderi = { id: 456 };
        expectedResult = service.addTenderiToCollectionIfMissing([], tenderi, tenderi2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenderi);
        expect(expectedResult).toContain(tenderi2);
      });

      it('should accept null and undefined values', () => {
        const tenderi: ITenderi = { id: 123 };
        expectedResult = service.addTenderiToCollectionIfMissing([], null, tenderi, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tenderi);
      });

      it('should return initial array if no Tenderi is added', () => {
        const tenderiCollection: ITenderi[] = [{ id: 123 }];
        expectedResult = service.addTenderiToCollectionIfMissing(tenderiCollection, undefined, null);
        expect(expectedResult).toEqual(tenderiCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
