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
    if (StringUtils.isBlank(lookFor) || !node.getText().equalsIgnoreCase(lookFor)) {
      return;
    }

    Tree beginNode = findNextBeginNode(node);
    if (beginNode == null) {
      return;
    }

    if (!childrenContainType(beginNode, DelphiLexer.INHERITED)) {
      addViolation(ctx, node);
    }
  }

  private boolean childrenContainType(Tree node, int nodeType) {
    for (int i = 0; i < node.getChildCount(); i++) {
      if (node.getChild(i).getType() == nodeType) {
        return true;
      }
    }
    return false;
  }

  private Tree findNextBeginNode(DelphiPMDNode node) {
    for (int i = node.getChildIndex() + 1; isInSearchingRange(node, i); i++) {
      Tree child = node.getParent().getChild(i);
      if (child.getType() == DelphiLexer.BEGIN) {
        return child;
      }
    }
    return null;
  }

  private boolean isInSearchingRange(DelphiPMDNode node, int i) {
    return i < node.getChildIndex() + MAX_LOOK_AHEAD && i < node.getParent().getChildCount();
  }

}
