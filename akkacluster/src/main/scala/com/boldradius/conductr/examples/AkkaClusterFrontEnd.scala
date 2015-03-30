package com.boldradius.conductr.examples

import akka.actor._
import akka.routing.FromConfig
import akka.util.Timeout
import com.boldradius.auction.AuctionProtocol.GetBidHistoryQuery
import com.boldradius.auction.AuctionProtocol.PlaceBidCmd
import com.boldradius.auction.AuctionProtocol.StartAuctionCmd
import com.boldradius.auction.AuctionProtocol.WinningBidPriceQuery
import com.boldradius.simple.GetAuctionHistory
import com.boldradius.simple.GetWinningBid
import com.boldradius.simple.PlaceBid
import com.boldradius.simple.StartAuction



trait AuctionBackendRouterProvider {
  def newBackendRouter(context: ActorContext): ActorRef
}

trait AuctionBackendRouterProviderImpl extends AuctionBackendRouterProvider {
  def newBackendRouter(context: ActorContext) = context.actorOf(FromConfig.props(), name = "simpleAuctionBackendRouter")
}



class AkkaClusterFrontEnd() extends Actor with ActorLogging {

  this: AuctionBackendRouterProvider =>

  implicit val timeout = Timeout(5 seconds)

  val backendRouter = newBackendRouter(context)

  def receive = {
    case PlaceBidCmd(auctionId, buyer, bidPrice) =>
      backendRouter.forward(PlaceBid(auctionId, buyer, bidPrice))
    case StartAuctionCmd(auctionId, start, end, initialPrice, prodId) =>
      backendRouter.forward(StartAuction(auctionId, start, end, initialPrice, prodId))
    case WinningBidPriceQuery(auctionId) =>
      backendRouter.forward(GetWinningBid(auctionId))
    case GetBidHistoryQuery(auctionId) =>
      backendRouter.forward(GetAuctionHistory(auctionId))
    case ReceiveTimeout =>
      log.info("Timeout")
  }
}
object AkkaClusterFrontEnd extends App {

}
