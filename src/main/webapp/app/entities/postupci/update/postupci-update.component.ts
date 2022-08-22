import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPostupci, Postupci } from '../postupci.model';
import { PostupciService } from '../service/postupci.service';

@Component({
  selector: 'jhi-postupci-update',
  templateUrl: './postupci-update.component.html',
})
export class PostupciUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    sifraPostupka: [],
    opis: [],
  });

  constructor(protected postupciService: PostupciService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ postupci }) => {
      this.updateForm(postupci);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const postupci = this.createFromForm();
    if (postupci.id !== undefined) {
      this.subscribeToSaveResponse(this.postupciService.update(postupci));
    } else {
      this.subscribeToSaveResponse(this.postupciService.create(postupci));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPostupci>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(postupci: IPostupci): void {
    this.editForm.patchValue({
      id: postupci.id,
      sifraPostupka: postupci.sifraPostupka,
      opis: postupci.opis,
    });
  }

  protected createFromForm(): IPostupci {
    return {
      ...new Postupci(),
      id: this.editForm.get(['id'])!.value,
      sifraPostupka: this.editForm.get(['sifraPostupka'])!.value,
      opis: this.editForm.get(['opis'])!.value,
    };
  }
}
