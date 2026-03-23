export type GameLevel = 'EASY' | 'NORMAL' | 'HARD' | 'MASTERMIND';

export type GameStatus = 'IN_PROGRESS' | 'WON' | 'LOST' | 'GAVE_UP';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
}

export interface SignupRequest {
  name: string;
  password: string;
  email: string;
  age: number;
  nickname: string;
}

export interface Profile {
  age: number;
  email: string;
  name: string;
  nickname: string;
}

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

export interface ApiErrorResponse {
  status?: number;
  error?: string;
}

export interface Tip {
  correctPositions: number;
  correctColors: number;
}

export interface GuessRow {
  guess: number[];
  tips: Tip;
}

export interface GameBoardState {
  status: GameStatus;
  gameLevel: GameLevel;
  numberOfColumnColors: number;
  maximumOfAttempts: number;
  repeatedColorAllowed: boolean;
  rows: GuessRow[];
  secret?: number[];
}

export interface GuessOutcome {
  status: GameStatus;
  gameLevel: GameLevel;
  tips?: Tip;
  secret?: number[];
}

export interface HistorySummaryItem {
  publicUuid: string;
  level: GameLevel;
  pointsMaked: number;
  status: GameStatus;
  attemptsUsed: number;
  createdAt: string | null;
  finishedAt: string | null;
}

export interface HistoryResponse {
  gameHistoryBestGames: HistorySummaryItem[];
  gameHistoryFull: HistorySummaryItem[];
}

export interface RankingItem {
  gameUuidPublic: string;
  userNickname: string;
  gameLevel: GameLevel;
  pointsMaked: number;
  attemptsUsed: number;
  createdAt: string | null;
  finishedAt: string | null;
}

export interface RankingResponse {
  top10EasyGames: RankingItem[];
  top10NormalGames: RankingItem[];
  top10HardGames: RankingItem[];
  top10MastermindGames: RankingItem[];
}

export interface LevelOption {
  level: GameLevel;
  label: string;
  description: string;
  columns: number;
  repeatedColors: boolean;
}

export interface AuthSession {
  token: string;
  tokenType: string;
  username: string;
  password: string;
}