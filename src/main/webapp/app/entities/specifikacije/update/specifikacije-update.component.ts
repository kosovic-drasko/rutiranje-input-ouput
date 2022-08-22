import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISpecifikacije, Specifikacije } from '../specifikacije.model';
import { SpecifikacijeService } from '../service/specifikacije.service';

@Component({
  selector: 'jhi-specifikacije-update',
  templateUrl: './specifikacije-update.component.html',
})
export class SpecifikacijeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    sifraPostupka: [null, [Validators.required]],
    naziv: [],
  });

  constructor(protected specifikacijeService: SpecifikacijeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ specifikacije }) => {
      this.updateForm(specifikacije);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const specifikacije = this.createFromForm();
    if (specifikacije.id !== undefined) {
      this.subscribeToSaveResponse(this.specifikacijeService.update(specifikacije));
    } else {
      this.subscribeToSaveResponse(this.specifikacijeService.create(specifikacije));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpecifikacije>>): void {
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

  protected updateForm(specifikacije: ISpecifikacije): void {
    this.editForm.patchValue({
      id: specifikacije.id,
      sifraPostupka: specifikacije.sifraPostupka,
      naziv: specifikacije.naziv,
    });
  }

  protected createFromForm(): ISpecifikacije {
    return {
      ...new Specifikacije(),
      id: this.editForm.get(['id'])!.value,
      sifraPostupka: this.editForm.get(['sifraPostupka'])!.value,
      naziv: this.editForm.get(['naziv'])!.value,
    };
  }
}
