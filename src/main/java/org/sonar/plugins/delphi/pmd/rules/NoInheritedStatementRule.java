/*
 * Sonar Delphi Plugin
 * Copyright (C) 2011 Sabre Airline Solutions and Fabricio Colombo
 * Author(s):
 * Przemyslaw Kociolek (przemyslaw.kociolek@sabre.com)
 * Michal Wojcik (michal.wojcik@sabre.com)
 * Fabricio Colombo (fabricio.colombo.mva@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.delphi.pmd.rules;

import org.antlr.runtime.tree.Tree;
import org.apache.commons.lang.StringUtils;
import org.sonar.plugins.delphi.antlr.DelphiLexer;
import org.sonar.plugins.delphi.antlr.ast.DelphiPMDNode;

import net.sourceforge.pmd.RuleContext;

/**
 * Class that checks if 'inherited' statement is in some function or procedure.
 * If no, it triggers a violation.
 */
public class NoInheritedStatementRule extends DelphiRule {

  private static final int MAX_LOOK_AHEAD = 3;
  private String lookFor = "";

  public void setLookFor(String lookFor) {
    this.lookFor = lookFor;
  }

  @Override
  public void visit(DelphiPMDNode node, RuleContext ctx) {
    if (StringUtils.isEmpty(lookFor) || !node.getText().equalsIgnoreCase(lookFor)) {
      return;
    }

    Tree parentNode = node.getParent();
    int nodeIndex = node.getChildIndex();
    int maxIndex = Math.min(nodeIndex + MAX_LOOK_AHEAD, parentNode.getChildCount());

    Tree beginNode = getFirstChildWithType(parentNode, DelphiLexer.BEGIN, nodeIndex + 1, maxIndex);
    if (beginNode == null) {
      return;
    }

    if (getFirstChildWithType(beginNode, DelphiLexer.INHERITED) == null) {
      addViolation(ctx, node);
    }
  }

  private Tree getFirstChildWithType(Tree node, int nodeType) {
    return getFirstChildWithType(node, nodeType, 0, node.getChildCount());
  }

  private Tree getFirstChildWithType(Tree node, int nodeType, int startIndex, int maxIndex) {
    for (int i = startIndex; i < maxIndex; ++i) {
      if (node.getChild(i).getType() == nodeType) {
        return node.getChild(i);
      }
    }
    return null;
  }
}
