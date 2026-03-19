import type { ThemeEnum, UserRole } from "../utils/enums";

export interface UserStats {
    totalGamesPlayed: number;
    totalBingos: number;
    totalSlotsMarked: number;
}
  
export interface User {
    id: string;
    username: string;
    email?: string;
    role: UserRole;
    position?: string;
    careerXp: number;
    seasonXp: number;
    preferredTheme: ThemeEnum;
    suspended: boolean;
    stats?: UserStats;
}
  
export interface BingoSlot {
    position: number;
    phrase: string;
    marked: boolean;
    verified: boolean;
}
  
export interface BingoCard {
    userId: string;
    username: string;
    gameDate: string;
    slots: BingoSlot[];
    markedCount: number;
    completed: boolean;
}
  
export interface Phrase {
    id: string;
    text: string;
    active: boolean;
}