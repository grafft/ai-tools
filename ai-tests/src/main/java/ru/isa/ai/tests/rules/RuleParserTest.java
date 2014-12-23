package ru.isa.ai.tests.rules;

import jadex.rules.parser.conditions.ParserHelper;
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
 * Created by GraffT on 08.12.2014.
 * for ai-main
 */
public class RuleParserTest {
    public static final OAVTypeModel car_type_model;
    public static final OAVObjectType car_type;
    public static final OAVAttributeType car_position;

    static {
        car_type_model = new OAVTypeModel("car_type_model");
        car_type_model.addTypeModel(OAVJavaType.java_type_model);
        car_type = car_type_model.createType("car");
        car_position = car_type.createAttributeType("position", OAVJavaType.java_integer_type);
    }

    public static void main(String[] args) {
        RuleSystem system = createReteSystem();
        RuleSystemExecutor exe = new RuleSystemExecutor(system, true);
        RuleEnginePanel.createRuleEngineFrame(exe, "Blocksworld Test");

        initStartSituation(system);
    }

    private static void initStartSituation(RuleSystem system) {
        IOAVState state = system.getState();
        Object b1 = state.createRootObject(car_type);
        Object b2 = state.createRootObject(car_type);

        state.setAttributeValue(b1, car_position, 5);
        state.setAttributeValue(b2, car_position, 6);
        state.notifyEventListeners();
        System.out.println("Rete memory: " + ((RetePatternMatcherState) system.getMatcherState()).getReteMemory());
    }

    public static RuleSystem createReteSystem() {
        ICondition condition = ParserHelper.parseJavaCondition(
                "car x && car y && x != y && (x.position >= 6 || y.position >= 5)",
                car_type_model);

        IAction action = new IAction() {
            public void execute(IOAVState state, IVariableAssignments assigments) {
                Object carX = assigments.getVariableValue("x");
                Object carY = assigments.getVariableValue("y");
                System.out.println("TRIGGERED: x=" + carX + ", y=" + carY);

                state.setAttributeValue(carX, car_position, 5);
                state.setAttributeValue(carY, car_position, 6);

                state.notifyEventListeners();
            }
        };

        IRule rule = new Rule("car", condition, action);
        Rulebase rb = new Rulebase();
        IPatternMatcherFunctionality pf = new RetePatternMatcherFunctionality(rb);

        RuleSystem system = new RuleSystem(OAVStateFactory.createOAVState(car_type_model), rb, pf);
        system.init();
        system.getRulebase().addRule(rule);

        return system;
    }
}
