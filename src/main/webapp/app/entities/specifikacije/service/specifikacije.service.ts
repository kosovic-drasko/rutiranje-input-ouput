import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpecifikacije, getSpecifikacijeIdentifier } from '../specifikacije.model';

export type EntityResponseType = HttpResponse<ISpecifikacije>;
export type EntityArrayResponseType = HttpResponse<ISpecifikacije[]>;

@Injectable({ providedIn: 'root' })
export class SpecifikacijeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/specifikacijes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(specifikacije: ISpecifikacije): Observable<EntityResponseType> {
    return this.http.post<ISpecifikacije>(this.resourceUrl, specifikacije, { observe: 'response' });
  }

  update(specifikacije: ISpecifikacije): Observable<EntityResponseType> {
    return this.http.put<ISpecifikacije>(`${this.resourceUrl}/${getSpecifikacijeIdentifier(specifikacije) as number}`, specifikacije, {
      observe: 'response',
    });
  }

  partialUpdate(specifikacije: ISpecifikacije): Observable<EntityResponseType> {
    return this.http.patch<ISpecifikacije>(`${this.resourceUrl}/${getSpecifikacijeIdentifier(specifikacije) as number}`, specifikacije, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISpecifikacije>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpecifikacije[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSpecifikacijeToCollectionIfMissing(
    specifikacijeCollection: ISpecifikacije[],
    ...specifikacijesToCheck: (ISpecifikacije | null | undefined)[]
  ): ISpecifikacije[] {
    const specifikacijes: ISpecifikacije[] = specifikacijesToCheck.filter(isPresent);
    if (specifikacijes.length > 0) {
      const specifikacijeCollectionIdentifiers = specifikacijeCollection.map(
        specifikacijeItem => getSpecifikacijeIdentifier(specifikacijeItem)!
      );
      const specifikacijesToAdd = specifikacijes.filter(specifikacijeItem => {
        const specifikacijeIdentifier = getSpecifikacijeIdentifier(specifikacijeItem);
        if (specifikacijeIdentifier == null || specifikacijeCollectionIdentifiers.includes(specifikacijeIdentifier)) {
          return false;
        }
        specifikacijeCollectionIdentifiers.push(specifikacijeIdentifier);
        return true;
      });
      return [...specifikacijesToAdd, ...specifikacijeCollection];
    }
    return specifikacijeCollection;
  }
}
