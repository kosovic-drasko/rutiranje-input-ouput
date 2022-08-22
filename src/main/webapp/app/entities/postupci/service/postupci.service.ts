import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPostupci, getPostupciIdentifier } from '../postupci.model';

export type EntityResponseType = HttpResponse<IPostupci>;
export type EntityArrayResponseType = HttpResponse<IPostupci[]>;

@Injectable({ providedIn: 'root' })
export class PostupciService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/postupcis');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(postupci: IPostupci): Observable<EntityResponseType> {
    return this.http.post<IPostupci>(this.resourceUrl, postupci, { observe: 'response' });
  }

  update(postupci: IPostupci): Observable<EntityResponseType> {
    return this.http.put<IPostupci>(`${this.resourceUrl}/${getPostupciIdentifier(postupci) as number}`, postupci, { observe: 'response' });
  }

  partialUpdate(postupci: IPostupci): Observable<EntityResponseType> {
    return this.http.patch<IPostupci>(`${this.resourceUrl}/${getPostupciIdentifier(postupci) as number}`, postupci, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPostupci>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPostupci[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPostupciToCollectionIfMissing(postupciCollection: IPostupci[], ...postupcisToCheck: (IPostupci | null | undefined)[]): IPostupci[] {
    const postupcis: IPostupci[] = postupcisToCheck.filter(isPresent);
    if (postupcis.length > 0) {
      const postupciCollectionIdentifiers = postupciCollection.map(postupciItem => getPostupciIdentifier(postupciItem)!);
      const postupcisToAdd = postupcis.filter(postupciItem => {
        const postupciIdentifier = getPostupciIdentifier(postupciItem);
        if (postupciIdentifier == null || postupciCollectionIdentifiers.includes(postupciIdentifier)) {
          return false;
        }
        postupciCollectionIdentifiers.push(postupciIdentifier);
        return true;
      });
      return [...postupcisToAdd, ...postupciCollection];
    }
    return postupciCollection;
  }
}
