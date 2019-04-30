package org.rob.notes.responsebuilders.markdown;

import org.commonmark.ext.heading.anchor.IdGenerator;
import org.commonmark.node.*;

public class ContentsBuilder extends AbstractVisitor {

    private static class ContentsBlockBuilder {

        private static class BulletListNode {
            private final int level;
            private final BulletListNode parent;
            private final BulletList bulletList;

            public BulletListNode(BulletListNode parent, BulletList bulletList) {
                this.parent = parent;
                this.bulletList = bulletList;
                this.level = parent == null ? 1 : parent.level + 1;
            }
        }

        private final IdGenerator idGenerator = new IdGenerator.Builder().build();
        private BulletListNode head;

        public ListItem withListItem(int level, String value) {
            Link link = new Link('#' + idGenerator.generateId(value), null);
            link.appendChild(new Text(value));

            ListItem item1 = new ListItem();
            item1.appendChild(link);

            bulletList(level).appendChild(item1);
            return item1;
        }

        public BulletList build() {
            return bulletList(1);
        }

        private BulletList bulletList(int level) {
            if (head == null) {
                head = new BulletListNode(null, new BulletList());
            }
            while (head.level < level) {
                head = new BulletListNode(head, new BulletList());
                head.parent.bulletList.appendChild(head.bulletList);
            }
            while (head.level > level) {
                head = head.parent;
            }
            return head.bulletList;
        }
    }

    private final ContentsBlockBuilder contentsBlockBuilder = new ContentsBlockBuilder();

    @Override
    public void visit(Heading heading) {
        Node firstChild = heading.getFirstChild();
        if (firstChild instanceof Text) {
            Text text = (Text)firstChild;
            contentsBlockBuilder.withListItem(heading.getLevel(), text.getLiteral());
        }
        this.visitChildren(heading);
    }

    public Document document() {
        Heading heading = new Heading();
        heading.setLevel(1);
        heading.appendChild(new Text("Contents"));

        Document document = new Document();
        document.appendChild(heading);
        document.appendChild(contentsBlockBuilder.build());
        return document;
    }

}
