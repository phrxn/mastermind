import {
  GameBoardState,
  GameLevel,
  GameStatus,
  GuessOutcome,
  GuessRow,
  HistoryResponse,
  HistorySummaryItem,
  LevelOption,
  RankingItem,
  RankingResponse,
  Tip
} from '../models/mastermind.models';

const LEVEL_MAP: Record<string, GameLevel> = {
  '0': 'EASY',
  '1': 'EASY',
  '2': 'NORMAL',
  '3': 'HARD',
  '4': 'MASTERMIND',
  EASY: 'EASY',
  NORMAL: 'NORMAL',
  HARD: 'HARD',
  MASTERMIND: 'MASTERMIND'
};

const STATUS_MAP: Record<string, GameStatus> = {
  '1': 'IN_PROGRESS',
  '2': 'WON',
  '3': 'LOST',
  '4': 'GAVE_UP',
  GAME_IN_PROGRESS: 'IN_PROGRESS',
  IN_PROGRESS: 'IN_PROGRESS',
  GAME_WIN: 'WON',
  WON: 'WON',
  GAME_OVER: 'LOST',
  LOST: 'LOST',
  GAME_GIVE_UP: 'GAVE_UP',
  GAVE_UP: 'GAVE_UP'
};

export const levelOptions: LevelOption[] = [
  {
    level: 'EASY',
    label: 'Easy',
    description: '4 cores, sem repeticao e uma partida mais rapida.',
    columns: 4,
    repeatedColors: false
  },
  {
    level: 'NORMAL',
    label: 'Normal',
    description: '4 cores com repeticao liberada para aumentar a leitura do tabuleiro.',
    columns: 4,
    repeatedColors: true
  },
  {
    level: 'HARD',
    label: 'Hard',
    description: '6 cores sem repeticao e mais combinacoes para testar memoria.',
    columns: 6,
    repeatedColors: false
  },
  {
    level: 'MASTERMIND',
    label: 'Mastermind',
    description: '6 cores, repeticao liberada e o desafio completo.',
    columns: 6,
    repeatedColors: true
  }
];

export const levelLabels: Record<GameLevel, string> = {
  EASY: 'Easy',
  NORMAL: 'Normal',
  HARD: 'Hard',
  MASTERMIND: 'Mastermind'
};

export const statusLabels: Record<GameStatus, string> = {
  IN_PROGRESS: 'Em andamento',
  WON: 'Vitoria',
  LOST: 'Derrota',
  GAVE_UP: 'Desistiu'
};

export const colorTokens = [1, 2, 3, 4, 5, 6];

export function normalizeLevel(value: unknown): GameLevel {
  const normalized = String(value ?? '').trim().toUpperCase();
  return LEVEL_MAP[normalized] ?? 'EASY';
}

export function normalizeStatus(value: unknown): GameStatus {
  const normalized = String(value ?? '').trim().toUpperCase();
  return STATUS_MAP[normalized] ?? 'IN_PROGRESS';
}

export function normalizeTips(value: unknown): Tip {
  const tip = (value as Partial<Tip>) ?? {};
  return {
    correctPositions: Number(tip.correctPositions ?? 0),
    correctColors: Number(tip.correctColors ?? 0)
  };
}

export function normalizeRow(value: unknown): GuessRow {
  const row = (value as Partial<GuessRow>) ?? {};
  return {
    guess: Array.isArray(row.guess) ? row.guess.map((item) => Number(item)) : [],
    tips: normalizeTips(row.tips)
  };
}

export function normalizeBoardState(value: unknown): GameBoardState {
  const source = (value as Record<string, unknown>) ?? {};
  return {
    status: normalizeStatus(source['status']),
    gameLevel: normalizeLevel(source['gameLevel']),
    numberOfColumnColors: Number(source['numberOfColumnColors'] ?? source['numberOfColumncolors'] ?? 4),
    maximumOfAttempts: Number(source['maximumOfAttempts'] ?? source['maximumOfattempts'] ?? 10),
    repeatedColorAllowed: Boolean(source['repeatedColorAllowed']),
    rows: Array.isArray(source['rows']) ? source['rows'].map((item) => normalizeRow(item)) : [],
    secret: Array.isArray(source['secret']) ? source['secret'].map((item) => Number(item)) : undefined
  };
}

export function normalizeGuessOutcome(value: unknown): GuessOutcome {
  const source = (value as Record<string, unknown>) ?? {};
  return {
    status: normalizeStatus(source['status']),
    gameLevel: normalizeLevel(source['gameLevel']),
    tips: source['tips'] ? normalizeTips(source['tips']) : undefined,
    secret: Array.isArray(source['secret']) ? source['secret'].map((item) => Number(item)) : undefined
  };
}

export function normalizeHistoryItem(value: unknown): HistorySummaryItem {
  const source = (value as Record<string, unknown>) ?? {};
  return {
    publicUuid: String(source['publicUuid'] ?? ''),
    level: normalizeLevel(source['level']),
    pointsMaked: Number(source['pointsMaked'] ?? 0),
    status: normalizeStatus(source['status']),
    attemptsUsed: Number(source['attemptsUsed'] ?? 0),
    createdAt: (source['createdAt'] as string | null) ?? null,
    finishedAt: (source['finishedAt'] as string | null) ?? null
  };
}

export function normalizeHistoryResponse(value: unknown): HistoryResponse {
  const source = (value as Record<string, unknown>) ?? {};
  return {
    gameHistoryBestGames: Array.isArray(source['gameHistoryBestGames'])
      ? source['gameHistoryBestGames'].map((item) => normalizeHistoryItem(item))
      : [],
    gameHistoryFull: Array.isArray(source['gameHistoryFull'])
      ? source['gameHistoryFull'].map((item) => normalizeHistoryItem(item))
      : []
  };
}

export function normalizeRankingItem(value: unknown): RankingItem {
  const source = (value as Record<string, unknown>) ?? {};
  return {
    gameUuidPublic: String(source['gameUuidPublic'] ?? ''),
    userNickname: String(source['userNickname'] ?? '-'),
    gameLevel: normalizeLevel(source['gameLevel']),
    pointsMaked: Number(source['pointsMaked'] ?? 0),
    attemptsUsed: Number(source['attemptsUsed'] ?? 0),
    createdAt: (source['createdAt'] as string | null) ?? null,
    finishedAt: (source['finishedAt'] as string | null) ?? null
  };
}

export function normalizeRankingResponse(value: unknown): RankingResponse {
  const source = (value as Record<string, unknown>) ?? {};
  return {
    top10EasyGamesList: Array.isArray(source['top10EasyGamesList'])
      ? source['top10EasyGamesList'].map((item) => normalizeRankingItem(item))
      : [],
    top10NormalGamesList: Array.isArray(source['top10NormalGamesList'])
      ? source['top10NormalGamesList'].map((item) => normalizeRankingItem(item))
      : [],
    top10HardGamesList: Array.isArray(source['top10HardGamesList'])
      ? source['top10HardGamesList'].map((item) => normalizeRankingItem(item))
      : [],
    top10MastermindGamesList: Array.isArray(source['top10MastermindGamesList'])
      ? source['top10MastermindGamesList'].map((item) => normalizeRankingItem(item))
      : []
  };
}

export function formatApiError(error: unknown, fallback = 'Nao foi possivel concluir a operacao.'): string {
  if (!error || typeof error !== 'object') {
    return fallback;
  }

  const maybeMessage = (error as { error?: { error?: string }; message?: string }).error?.error;
  if (maybeMessage) {
    return maybeMessage;
  }

  return (error as { message?: string }).message ?? fallback;
}

export function joinUrl(baseUrl: string, path: string): string {
  return `${baseUrl.replace(/\/+$/, '')}/${path.replace(/^\/+/, '')}`;
}

export function buildGuessSlots(total: number, currentGuess: number[]): Array<number | null> {
  return Array.from({ length: total }, (_, index) => currentGuess[index] ?? null);
}

export function nextGuessSelection(
  currentGuess: number[],
  color: number,
  total: number,
  repeatedColorAllowed: boolean
): number[] {
  if (!repeatedColorAllowed && currentGuess.includes(color)) {
    return currentGuess;
  }

  if (currentGuess.length >= total) {
    return currentGuess;
  }

  return [...currentGuess, color];
}

export function removeGuessSelection(currentGuess: number[], index: number): number[] {
  return currentGuess.filter((_, slotIndex) => slotIndex !== index);
}

export function buildTipsGrid(tips: Tip, total: number): Array<'position' | 'color' | 'empty'> {
  const filled = [
    ...Array.from({ length: tips.correctPositions }, () => 'position' as const),
    ...Array.from({ length: tips.correctColors }, () => 'color' as const)
  ];

  return [...filled, ...Array.from({ length: Math.max(total - filled.length, 0) }, () => 'empty' as const)];
}