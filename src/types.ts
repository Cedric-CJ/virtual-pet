
export interface PetStats {
    Energie: number;
    Hunger: number;
    Durst: number;
    Komfort: number;
}

export interface PetData {
    name: string;
    type: string;
    stats: PetStats;
    createdDate: string;
    lastFed: string;
    lastWatered: string;
    lastSlept: string;
    lastPetted: string;
    lastShowered: string;
    username: string;
    userId: string | null;
}