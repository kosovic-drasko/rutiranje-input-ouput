export interface IPonude {
  id?: number;
  sifraPostupka?: number;
  naziv?: string | null;
}

export class Ponude implements IPonude {
  constructor(public id?: number, public sifraPostupka?: number, public naziv?: string | null) {}
}

export function getPonudeIdentifier(ponude: IPonude): number | undefined {
  return ponude.id;
}
