import { createContext, useContext } from 'react'
import { MarketStore } from './MarketStore'
import { PlayerStore } from './PlayerStore'
import { TradeStore } from './TradeStore'
import { GameStore } from './GameStore'

export class RootStore {
  marketStore = new MarketStore()
  playerStore = new PlayerStore()
  tradeStore = new TradeStore()
  gameStore = new GameStore()
}

const rootStore = new RootStore()
export const StoreContext = createContext(rootStore)

export function useStore() {
  return useContext(StoreContext)
}
