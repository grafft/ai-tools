package ru.isa.ai.causal.classifiers;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GraffT on 01.05.2014.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AQClassDescription {
    @XmlAttribute
    private String className;
    @XmlElementWrapper
    @XmlElement
    private List<CRProperty> description = new ArrayList<>();

    public AQClassDescription() {
    }

    public AQClassDescription(String className) {
        this.className = className;
    }

    public List<CRProperty> getDescription() {
        return description;
    }

    public String getClassName() {
        return className;
    }

    public static AQClassDescription createFromRules(List<AQRule> rules, int maximumDescriptionSize, String className) {
        List<CRProperty> rawDescription = new ArrayList<>();
        over:
        for (AQRule rule : rules) {
            // добавялем каждое свойство из правила с проверкой
            for (Map.Entry<CRFeature, List<Integer>> ruleEntry : rule.getTokens().entrySet()) {
                if (rawDescription.size() >= maximumDescriptionSize)
                    break over;
                CRProperty prop = new CRProperty(ruleEntry.getKey(), ruleEntry.getValue());
                prop.setPopularity(rule.getCoveredInstances().size());
                if (!rawDescription.contains(prop))
                    rawDescription.add(prop);
            }
        }
        return createFromProperties(rawDescription, className);
    }

    public static AQClassDescription createFromProperties(List<CRProperty> toClear, String className) {
        AQClassDescription classDescription = new AQClassDescription(className);

        int equalsPropsCount = 0;
        int collinearPropsCount = 0;
        int conflictedPropsCount = 0;

        // проверки: уникальность, коллинераность, конфликтность
        for (CRProperty propToCheck : toClear) {
            boolean toAdd = true;
            CRProperty oldToRemove = null;
            for (CRProperty existedProp : classDescription.description) {
                if (existedProp.equals(propToCheck)) {
                    toAdd = false;
                    break;
                } else if (existedProp.getFeature().equals(propToCheck.getFeature())) {
                    int collinearity = existedProp.collinearity(propToCheck);
                    // Улыб=1V2 > Улыб=1 - оставляем Улыб=1V2
                    if (collinearity == 1) {
                        collinearPropsCount++;
                        oldToRemove = existedProp;
                        break;
                    } else if (collinearity == -1) {
                        collinearPropsCount++;
                        toAdd = false;
                        break;
                    } else {
                        // оставляем более популярное
                        conflictedPropsCount++;
                        if (propToCheck.getPopularity() > existedProp.getPopularity()) {
                            oldToRemove = existedProp;
                            break;
                        } else {
                            toAdd = false;
                            break;
                        }
                    }
                }
            }

            if (toAdd)
                classDescription.description.add(propToCheck);
            if (oldToRemove != null)
                classDescription.description.remove(oldToRemove);
        }
        return classDescription;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Class ").append(className).append(":\n");
        for (int i = 0; i < description.size(); i++) {
            builder.append("\t").append(description.get(i).getPopularity()).append(" - ").append(description.get(i).toString());
            if (i < description.size() - 1)
                builder.append("\n");
        }
        return builder.toString();
    }
}
