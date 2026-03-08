import { useState, useEffect } from 'react'
import { observer } from 'mobx-react-lite'
import { Modal, InputNumber, Typography, Divider, App } from 'antd'
import { useStore } from '../stores/RootStore'
import type { Stock } from '../types'

const { Text } = Typography

interface TradeDialogProps {
  stock: Stock | null
  type: 'BUY' | 'SELL'
  onClose: () => void
}

const TradeDialog = observer(({ stock, type, onClose }: TradeDialogProps) => {
  const { tradeStore, playerStore, marketStore } = useStore()
  const { message } = App.useApp()
  const [quantity, setQuantity] = useState<number>(1)

  useEffect(() => {
    setQuantity(1)
  }, [stock, type])

  if (!stock) return null

  const isBuy = type === 'BUY'
  const total = stock.price * quantity

  const ownedEntry = playerStore.player?.portfolio?.find((p) => p.symbol === stock.symbol)
  const ownedQuantity = ownedEntry?.quantity ?? 0
  const availableCash = playerStore.player?.cash ?? 0

  const maxBuyQuantity = stock.price > 0 ? Math.floor(availableCash / stock.price) : 0
  const maxQuantity = isBuy ? maxBuyQuantity : ownedQuantity

  const handleConfirm = async () => {
    try {
      if (isBuy) {
        await tradeStore.buy(stock.symbol, quantity)
        message.success(`${quantity}x ${stock.symbol} gekauft für $${total.toFixed(2)}`)
      } else {
        await tradeStore.sell(stock.symbol, quantity)
        message.success(`${quantity}x ${stock.symbol} verkauft für $${total.toFixed(2)}`)
      }
      await playerStore.fetchPortfolio()
      await marketStore.fetchStocks()
      onClose()
    } catch (e: unknown) {
      const err = e as { response?: { data?: { message?: string } } }
      message.error(err.response?.data?.message ?? 'Trade fehlgeschlagen')
    }
  }

  return (
    <Modal
      title={`${isBuy ? 'Kaufen' : 'Verkaufen'}: ${stock.name} (${stock.symbol})`}
      open={!!stock}
      onOk={handleConfirm}
      onCancel={onClose}
      okText={isBuy ? 'Kaufen' : 'Verkaufen'}
      okButtonProps={{
        danger: !isBuy,
        disabled: quantity < 1 || quantity > maxQuantity,
        loading: tradeStore.loading,
      }}
      cancelText="Abbrechen"
    >
      <div style={{ marginBottom: 16 }}>
        <Text type="secondary">Aktueller Kurs:</Text>{' '}
        <Text strong>${stock.price.toFixed(2)}</Text>
      </div>

      <div style={{ marginBottom: 16 }}>
        {isBuy ? (
          <>
            <Text type="secondary">Verfügbares Cash:</Text>{' '}
            <Text strong>${availableCash.toFixed(2)}</Text>
          </>
        ) : (
          <>
            <Text type="secondary">Im Besitz:</Text>{' '}
            <Text strong>{ownedQuantity} Aktien</Text>
          </>
        )}
      </div>

      <div style={{ marginBottom: 16 }}>
        <Text type="secondary">Anzahl:</Text>
        <InputNumber
          min={1}
          max={maxQuantity}
          value={quantity}
          onChange={(val) => setQuantity(val ?? 1)}
          style={{ width: '100%', marginTop: 8 }}
        />
        {maxQuantity > 0 && (
          <Text type="secondary" style={{ fontSize: 12 }}>
            Max: {maxQuantity}
          </Text>
        )}
      </div>

      <Divider />

      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
        <Text strong>Gesamtpreis:</Text>
        <Text strong style={{ fontSize: 18 }}>
          ${total.toFixed(2)}
        </Text>
      </div>
    </Modal>
  )
})

export default TradeDialog
