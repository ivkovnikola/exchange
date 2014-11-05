/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.trade.protocol.createoffer.tasks;

import io.bitsquare.btc.WalletFacade;
import io.bitsquare.util.task.FaultHandler;
import io.bitsquare.util.task.ResultHandler;

import org.bitcoinj.core.Transaction;

import com.google.common.util.concurrent.FutureCallback;

import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BroadCastOfferFeeTx {
    private static final Logger log = LoggerFactory.getLogger(BroadCastOfferFeeTx.class);

    public static void run(ResultHandler resultHandler, FaultHandler faultHandler, WalletFacade walletFacade,
                           Transaction tx) {
        try {
            walletFacade.broadcastCreateOfferFeeTx(tx, new FutureCallback<Transaction>() {
                @Override
                public void onSuccess(@javax.annotation.Nullable Transaction transaction) {
                    log.info("sendResult onSuccess:" + transaction);
                    if (transaction != null) {
                        try {
                            resultHandler.handleResult();
                        } catch (Exception e) {
                            faultHandler.handleFault("Offer fee payment failed.", e);
                        }
                    }
                    else {
                        faultHandler.handleFault("Offer fee payment failed.",
                                new Exception("Offer fee payment failed. Transaction = null."));
                    }
                }

                @Override
                public void onFailure(@NotNull Throwable t) {
                    faultHandler.handleFault("Offer fee payment failed with an exception.", t);
                }
            });
        } catch (Throwable t) {
            faultHandler.handleFault("Offer fee payment failed because an exception occurred.", t);
        }
    }
}
