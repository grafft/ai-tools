package ru.isa.ai.tests.rules;

import jadex.rules.rulesystem.*;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rete.RetePatternMatcherState;
import jadex.rules.rulesystem.rules.*;
import jadex.rules.state.*;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

import java.util.Iterator;
import java.util.List;

/**
 * Created by GraffT on 01.12.2014.
 * for ai-main
 */
public class JadexBlockWorld {
    public static final OAVTypeModel blocksworld_type_model;
    public static final OAVObjectType block_type;
    public static final OAVAttributeType block_has_name;
    public static final OAVAttributeType block_has_color;
    public static final OAVAttributeType block_has_on;
    public static final OAVAttributeType block_has_left;

    static {
        blocksworld_type_model = new OAVTypeModel("blocksworld_type_model");
        blocksworld_type_model.addTypeModel(OAVJavaType.java_type_model);

        // block
        block_type = blocksworld_type_model.createType("block");
        block_has_name = block_type.createAttributeType("block_has_name", OAVJavaType.java_string_type);
        block_has_color = block_type.createAttributeType("block_has_color", OAVJavaType.java_string_type);
        block_has_on = block_type.createAttributeType("block_has_on", block_type, OAVAttributeType.LIST);
        block_has_left = block_type.createAttributeType("block_has_left", block_type, OAVAttributeType.LIST);
    }

    public static void main(String[] args) {
        RuleSystem system = createReteSystem();
        RuleSystemExecutor exe = new RuleSystemExecutor(system, true);
        RuleEnginePanel.createRuleEngineFrame(exe, "Blocksworld Test");
        IOAVState state = system.getState();

        Object b1 = state.createRootObject(block_type);
        Object b2 = state.createRootObject(block_type);
        Object b3 = state.createRootObject(block_type);
        Object b4 = state.createRootObject(block_type);
        Object b5 = state.createRootObject(block_type);

        state.setAttributeValue(b1, block_has_name, "B1");
        state.setAttributeValue(b1, block_has_color, "red");
        state.addAttributeValue(b1, block_has_on, b2);
        state.addAttributeValue(b1, block_has_on, b3);
        state.addAttributeValue(b1, block_has_on, b4);
        state.addAttributeValue(b1, block_has_left, b5);

        state.setAttributeValue(b2, block_has_name, "B2");
        state.setAttributeValue(b2, block_has_color, "green");
        state.addAttributeValue(b2, block_has_on, b3);
        state.addAttributeValue(b2, block_has_on, b4);
        state.addAttributeValue(b2, block_has_left, b5);

        state.setAttributeValue(b3, block_has_name, "B3");
        state.setAttributeValue(b3, block_has_color, "blue");
        state.addAttributeValue(b3, block_has_on, b4);
        state.addAttributeValue(b3, block_has_left, b5);

        state.setAttributeValue(b4, block_has_name, "B4");
        state.setAttributeValue(b4, block_has_color, "yellow");
        state.addAttributeValue(b4, block_has_left, b5);

        state.setAttributeValue(b5, block_has_name, "B5");
        state.setAttributeValue(b5, block_has_color, "red");

        state.notifyEventListeners();
        System.out.println("Rete memory: " + ((RetePatternMatcherState) system.getMatcherState()).getReteMemory());
    }

    public static RuleSystem createReteSystem() {
        ObjectCondition yc = new ObjectCondition(block_type);
        yc.addConstraint(new LiteralConstraint(block_has_color, "yellow"));
        yc.addConstraint(new BoundConstraint(null, new Variable("y", block_type)));

        ObjectCondition xc = new ObjectCondition(block_type);
        xc.addConstraint(new BoundConstraint(block_has_on, new Variable("y", block_type), IOperator.CONTAINS));
        xc.addConstraint(new BoundConstraint(null, new Variable("x", block_type)));

        AndCondition cond = new AndCondition(new ICondition[]{yc, xc});

        System.out.println(cond);

        IAction action = new IAction() {
            public void execute(IOAVState state, IVariableAssignments assigments) {
                Object blockX = assigments.getVariableValue("x");
                Object blockY = assigments.getVariableValue("y");
                System.out.println("TRIGGERED: x=" + blockX + ", y=" + blockY);

                Iterator it = state.getRootObjects();
                while (it.hasNext()) {
                    Object obj = it.next();
                    List blocksOn = ((List) state.getAttributeValue(obj, block_has_on));
                    if (blocksOn != null) {
                        if (blocksOn.contains(blockY)) {
                            state.removeAttributeValue(obj, block_has_on, blockY);
                        }
                    } else if (!obj.equals(blockY)) {
                        state.addAttributeValue(obj, block_has_on, blockY);
                    }
                }
            }
        };

        IRule rule = new Rule("block", cond, action);
        Rulebase rb = new Rulebase();
        IPatternMatcherFunctionality pf = new RetePatternMatcherFunctionality(rb);

        RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(blocksworld_type_model), rb, pf);
        system.init();
        system.getRulebase().addRule(rule);

        return system;
    }
}
