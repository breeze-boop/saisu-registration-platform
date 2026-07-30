import { Button, Card, Col, Input, Layout, List, Row, Space, Statistic, Tag, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { AgentReply, Shop, Voucher, api } from '../api/client';

const { Header, Content } = Layout;

export function App() {
  const [shops, setShops] = useState<Shop[]>([]);
  const [vouchers, setVouchers] = useState<Voucher[]>([]);
  const [question, setQuestion] = useState('这个优惠券还有库存吗？');
  const [reply, setReply] = useState<AgentReply | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.shops().then(setShops).catch(error => message.error(error.message));
    api.vouchers().then(setVouchers).catch(error => message.error(error.message));
  }, []);

  async function preload(id: number) {
    await api.preload(id);
    message.success('库存已预热到 Redis');
  }

  async function seckill(id: number) {
    const result = await api.seckill(id, 7);
    message.success(`${result.message}，订单号：${result.orderId}`);
  }

  async function askAgent() {
    setLoading(true);
    try { setReply(await api.chat(question)); }
    catch (error) { message.error(error instanceof Error ? error.message : '客服请求失败'); }
    finally { setLoading(false); }
  }

  return (
    <Layout className="page">
      <Header className="hero">
        <Typography.Title level={2} className="heroTitle">觅食 · 高并发秒杀平台</Typography.Title>
        <Typography.Text className="heroText">Spring Boot + Redis Lua + RabbitMQ + Caffeine + AgentScope Java</Typography.Text>
      </Header>
      <Content className="content">
        <Row gutter={[24, 24]}>
          <Col xs={24} lg={14}>
            <Card title="热门商家">
              <List dataSource={shops} renderItem={shop => (
                <List.Item>
                  <List.Item.Meta title={<Space>{shop.name}<Tag color="gold">{shop.score}</Tag></Space>} description={`${shop.category} · ${shop.address} · 人均 ¥${shop.avgPrice}`} />
                  <span>{shop.description}</span>
                </List.Item>
              )} />
            </Card>
          </Col>
          <Col xs={24} lg={10}>
            <Card title="秒杀优惠券">
              <List dataSource={vouchers} renderItem={voucher => (
                <List.Item actions={[
                  <Button key="preload" onClick={() => preload(voucher.id)}>预热库存</Button>,
                  <Button key="seckill" type="primary" onClick={() => seckill(voucher.id)}>立即秒杀</Button>
                ]}>
                  <List.Item.Meta title={voucher.title} description={`支付 ¥${voucher.payValue} 抵扣 ¥${voucher.actualValue}`} />
                  <Statistic title="库存" value={voucher.stock} />
                </List.Item>
              )} />
            </Card>
          </Col>
          <Col span={24}>
            <Card title="AI 智能客服（Java AgentScope 工具风格）">
              <Space.Compact className="agentInput">
                <Input value={question} onChange={event => setQuestion(event.target.value)} onPressEnter={askAgent} />
                <Button type="primary" loading={loading} onClick={askAgent}>咨询</Button>
              </Space.Compact>
              {reply && <Card className="reply"><p>{reply.answer}</p><Space><Tag color="blue">Tools: {reply.usedTools.join(', ')}</Tag><Tag color="purple">{reply.model}</Tag><Tag>{reply.elapsedMs}ms</Tag><Tag>{reply.estimatedTokens} tokens</Tag></Space></Card>}
            </Card>
          </Col>
        </Row>
      </Content>
    </Layout>
  );
}
