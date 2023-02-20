package com.ravingarinc.api.gui.api;

import com.ravingarinc.api.I;
import com.ravingarinc.api.gui.BaseGui;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Component implementation that specifies a GUI component that can have any amount of child components
 */
public abstract class Element implements Component {
    protected final String identifier;
    protected final String parent;
    protected final List<Supplier<Component>> toBeAdded;
    private final Integer priority;
    protected Map<String, Component> children;

    public Element(final String identifier, final String parent, final int priority) {
        this.identifier = identifier;
        this.parent = parent;
        children = new LinkedHashMap<>();
        toBeAdded = new LinkedList<>();
        this.priority = priority;
    }

    @Override
    @NotNull
    public Integer getPriority() {
        return priority;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public void fillElement(final BaseGui gui) {
        this.getChildren().forEach(element -> element.fillElement(gui));
    }

    /**
     * Searches for all components in this current element matching the type specified, does not search for children's children.
     *
     * @return A list of components with matching name
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> findAllComponents(final T type) {
        considerToBeAdded();

        final List<T> found = new LinkedList<>();
        children.values().forEach((component) -> {
            if (component.getThisClass() == type.getThisClass()) {
                found.add((T) component);
            }
        });
        return found;
    }

    /**
     * Find's any component of any type matching the identifier for THIS object's children. Does not search
     * it's children's children.
     *
     * @param identifier The identifier to match
     * @return An optional component
     */
    @NotNull
    public Optional<Component> findAnyComponent(final String identifier) {
        considerToBeAdded();
        return Optional.ofNullable(children.get(ChatColor.stripColor(identifier)));
    }

    /**
     * Finds the first child component of specified type
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> findFirstComponent(final T type) {
        considerToBeAdded();
        for (final Component c : children.values()) {
            if (type.getThisClass().isAssignableFrom(c.getThisClass())) {
                return Optional.of((T) c);
            }
        }
        return Optional.empty();
    }

    /**
     * Searches for a component of a given type matching a particular identifier. Searches recursively only if the type
     * is not menu.
     *
     * @param type
     * @param identifier
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T extends Component> Optional<T> findComponent(final T type, final String identifier) {
        considerToBeAdded();
        final Optional<? extends Component> component = Optional.ofNullable(children.get(ChatColor.stripColor(identifier)));

        Optional<T> finalComponent = Optional.empty();
        if (component.isPresent() && type.getThisClass().isAssignableFrom(component.get().getThisClass())) {
            finalComponent = Optional.of((T) component.get());
        } else {
            if (!MENU.isSameType(type)) { //If Menu, we don't want to search recursively.
                for (final Component c : children.values()) {
                    final Optional<T> optional = c.findComponent(type, identifier);
                    if (optional.isPresent()) {
                        finalComponent = optional;
                        break;
                    }
                }
            }
        }

        return finalComponent;
    }

    public final void addChild(final Supplier<Component> component) {
        toBeAdded.add(component);
    }

    protected void considerToBeAdded() {
        if (!toBeAdded.isEmpty()) {
            final List<Supplier<Component>> nextChildren = new LinkedList<>(toBeAdded);
            nextChildren.forEach(c -> {
                final Component child = c.get();
                final String id = child.getIdentifier();
                if (children.containsKey(id)) {
                    I.log(Level.WARNING, "Encountered duplicate component of " + id + " in element " + identifier);
                }
                children.put(id, child);
                toBeAdded.remove(c);
            });

            //Sort children.
            children = children.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(i -> i.getValue().getPriority()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));
        }
    }

    /**
     * Adds any children which have queued to be added before returning an ordered list of all children of this
     * component.
     *
     * @return List of children
     */
    public List<Component> getChildren() {
        considerToBeAdded();

        final List<Component> childs = new LinkedList<>(); //To maintain order
        for (final Map.Entry<String, Component> entry : children.entrySet()) {
            childs.add(entry.getValue());
        }
        return childs;
    }
}
