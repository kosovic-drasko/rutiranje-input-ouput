export interface ISpecifikacije {
  id?: number;
  sifraPostupka?: number;
  naziv?: string | null;
}

export class Specifikacije implements ISpecifikacije {
  constructor(public id?: number, public sifraPostupka?: number, public naziv?: string | null) {}
}

export function getSpecifikacijeIdentifier(specifikacije: ISpecifikacije): number | undefined {
  return specifikacije.id;
}
