import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TenderiService } from '../service/tenderi.service';

import { TenderiComponent } from './tenderi.component';

describe('Tenderi Management Component', () => {
  let comp: TenderiComponent;
  let fixture: ComponentFixture<TenderiComponent>;
  let service: TenderiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TenderiComponent],
    })
      .overrideTemplate(TenderiComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TenderiComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TenderiService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.tenderis?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
