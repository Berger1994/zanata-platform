/*
 * Copyright 2012, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.webtrans.shared.rpc;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.zanata.webtrans.shared.model.TransUnitUpdateInfo;
import com.google.common.collect.Lists;

/**
 * Action to revert translations to their state before they were updated.
 *
 * @author David Mason, damason@redhat.com
 */
public class RevertTransUnitUpdates extends
        AbstractWorkspaceAction<UpdateTransUnitResult> {
    private static final long serialVersionUID = 1L;

    @SuppressFBWarnings(value = "SE_BAD_FIELD")
    private List<TransUnitUpdateInfo> updatesToRevert;

    public RevertTransUnitUpdates() {
        updatesToRevert = new ArrayList<TransUnitUpdateInfo>();
    }

    public RevertTransUnitUpdates(List<TransUnitUpdateInfo> updatesToRevert) {
        this.updatesToRevert = Lists.newArrayList(updatesToRevert);
    }

    public void addUpdateToRevert(TransUnitUpdateInfo toRevert) {
        updatesToRevert.add(toRevert);
    }

    public List<TransUnitUpdateInfo> getUpdatesToRevert() {
        return updatesToRevert;
    }
}
