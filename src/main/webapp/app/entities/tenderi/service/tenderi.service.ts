import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITenderi, getTenderiIdentifier } from '../tenderi.model';

export type EntityResponseType = HttpResponse<ITenderi>;
export type EntityArrayResponseType = HttpResponse<ITenderi[]>;

@Injectable({ providedIn: 'root' })
export class TenderiService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tenderis');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITenderi>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITenderi[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  addTenderiToCollectionIfMissing(tenderiCollection: ITenderi[], ...tenderisToCheck: (ITenderi | null | undefined)[]): ITenderi[] {
    const tenderis: ITenderi[] = tenderisToCheck.filter(isPresent);
    if (tenderis.length > 0) {
      const tenderiCollectionIdentifiers = tenderiCollection.map(tenderiItem => getTenderiIdentifier(tenderiItem)!);
      const tenderisToAdd = tenderis.filter(tenderiItem => {
        const tenderiIdentifier = getTenderiIdentifier(tenderiItem);
        if (tenderiIdentifier == null || tenderiCollectionIdentifiers.includes(tenderiIdentifier)) {
          return false;
        }
        tenderiCollectionIdentifiers.push(tenderiIdentifier);
        return true;
      });
      return [...tenderisToAdd, ...tenderiCollection];
    }
    return tenderiCollection;
  }
}
