/*
 * Copyright 2019-2023 Shaun Laurens.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aeroncookbook.cluster.rfq.statemachine.actors;

public final class RfqSystem implements RfqActor
{
    public static final RfqSystem INSTANCE = new RfqSystem();

    private RfqSystem()
    {
        //nothing
    }

    @Override
    public boolean canCreate()
    {
        return false;
    }

    @Override
    public boolean canAccept()
    {
        return false;
    }

    @Override
    public boolean canReject()
    {
        return false;
    }

    @Override
    public boolean canQuote()
    {
        return false;
    }

    @Override
    public boolean canCounter()
    {
        return false;
    }

    @Override
    public boolean canExpire()
    {
        return true;
    }

    @Override
    public boolean canInvite()
    {
        return false;
    }

    @Override
    public boolean canComplete()
    {
        return true;
    }

    @Override
    public boolean canCancel()
    {
        return true;
    }

    @Override
    public boolean isResponder()
    {
        return false;
    }

    @Override
    public boolean isRequester()
    {
        return false;
    }

    @Override
    public boolean isSystem()
    {
        return true;
    }
}
