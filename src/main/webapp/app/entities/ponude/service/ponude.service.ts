import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPonude, getPonudeIdentifier } from '../ponude.model';

export type EntityResponseType = HttpResponse<IPonude>;
export type EntityArrayResponseType = HttpResponse<IPonude[]>;

@Injectable({ providedIn: 'root' })
export class PonudeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ponudes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ponude: IPonude): Observable<EntityResponseType> {
    return this.http.post<IPonude>(this.resourceUrl, ponude, { observe: 'response' });
  }

  update(ponude: IPonude): Observable<EntityResponseType> {
    return this.http.put<IPonude>(`${this.resourceUrl}/${getPonudeIdentifier(ponude) as number}`, ponude, { observe: 'response' });
  }

  partialUpdate(ponude: IPonude): Observable<EntityResponseType> {
    return this.http.patch<IPonude>(`${this.resourceUrl}/${getPonudeIdentifier(ponude) as number}`, ponude, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPonude>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPonude[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPonudeToCollectionIfMissing(ponudeCollection: IPonude[], ...ponudesToCheck: (IPonude | null | undefined)[]): IPonude[] {
    const ponudes: IPonude[] = ponudesToCheck.filter(isPresent);
    if (ponudes.length > 0) {
      const ponudeCollectionIdentifiers = ponudeCollection.map(ponudeItem => getPonudeIdentifier(ponudeItem)!);
      const ponudesToAdd = ponudes.filter(ponudeItem => {
        const ponudeIdentifier = getPonudeIdentifier(ponudeItem);
        if (ponudeIdentifier == null || ponudeCollectionIdentifiers.includes(ponudeIdentifier)) {
          return false;
        }
        ponudeCollectionIdentifiers.push(ponudeIdentifier);
        return true;
      });
      return [...ponudesToAdd, ...ponudeCollection];
    }
    return ponudeCollection;
  }
}
