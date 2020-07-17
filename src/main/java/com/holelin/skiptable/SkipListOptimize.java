package com.holelin.skiptable;

import java.util.Random;

/**
 * 1，跳表的一种实现方法，用于练习。跳表中存储的是正整数，并且存储的是不重复的。
 * 2，本类是参考作者zheng ，自己学习，优化了添加方法
 * 3，看完这个，我觉得再看ConcurrentSkipListMap 源码，会有很大收获
 * <p>
 * 参考文章链接:https://juejin.im/post/57fa935b0e3dd90057c50fbc
 * </p>
 * <p>
 * 补充:
 * 新增节点: 为了避免时间复杂度重新蜕化成O(n),SkipList不要求上下相邻两层链表之间的节点个数有严格的对应关系,
 * 而是为每个节点随机出一个层数(level).例: 一个节点随机出的层数是3,则就把它链入到第一层~第三层这三层链表中
 * <p>
 * 在分析之前，我们还需要着重指出的是，执行插入操作时计算随机数的过程，是一个很关键的过程，它对skiplist的统计特性有着很重要的影响。
 * 这并不是一个普通的服从均匀分布的随机数，它的计算过程如下：
 * <p>
 * 1. 首先，每个节点肯定都有第1层指针（每个节点都在第1层链表里）。
 * 2. 如果一个节点有第i层(i>=1)指针（即节点已经在第1层到第i层链表中），那么它有第(i+1)层指针的概率为p。
 * 3. 节点最大的层数不允许超过一个最大值，记为MaxLevel。
 * </p>

 * Author：ldb
 */

public class SkipListOptimize {

    public static final int MAX_LEVEL = 16;

    private int levelCount = 1;
    /**
     * 带头链表
     */
    private Node head = new Node(MAX_LEVEL);

    private Random random = new Random();

    public Node find(int value) {
        Node p = head;
        // 从最大层开始查找,找到前一节点,通过i--,移动到下层在开始查找
        for (int i = levelCount - 1; i >= 0; i--) {
            while (null != p.forwards[i] && p.forwards[i].data < value) {
                // 找到前一节点
                p = p.forwards[i];
            }
        }
        if (null != p.forwards[0] && p.forwards[0].data == value) {
            // 锁定最后一个节点
            return p.forwards[0];
        } else {
            // 未找到
            return null;
        }
    }

    public void insert(int value) {
        int level = null == head.forwards[0] ? 1 : randomLevel();
        // 如果条件满足 每次只增加一层
        if (level > levelCount) {
            level = ++levelCount;
        }
        Node newNode = new Node(level);
        newNode.data = value;
        Node[] update = new Node[level];
        for (int i = 0; i < level; i++) {
            update[i] = head;
        }
        Node p = head;
        // 从最大层开始查找,找到前一节点,通过i--,移动到下层再开始查找
        for (int i = levelCount; i >= 0; i--) {
            while (null != p.forwards[i] && p.forwards[i].data < value) {
                // 找到前一节点
                p = p.forwards[i];
            }
            // levelCount会大于level
            if (level > i) {
                update[i] = p;
            }
        }
        for (int i = 0; i < level; i++) {
            newNode.forwards[i] = update[i].forwards[i];
            update[i].forwards[i] = newNode;
        }
    }

    public void insertPlanB(int value) {
        int level = head.forwards[0] == null ? 1 : randomLevel();
        // 若条件满足则增加一层
        if (level > levelCount) {
            level = ++levelCount;
        }
        Node newNode = new Node(level);
        newNode.data = value;
        Node p = head;
        // 从最大层开始查找,找到前一节点,通过i--,移动下层在开始查找
        for (int i = levelCount - 1; i >= 0; i--) {
            while (null != p.forwards[i] && p.forwards[i].data < value) {
                // 找到前一节点
                p = p.forwards[i];
            }
            // levelCount会大于level
            if (level > i) {
                if (p.forwards[i] == null) {
                    p.forwards[i] = newNode;
                } else {
                    Node next = p.forwards[i];
                    p.forwards[i] = newNode;
                    newNode.forwards[i] = next;
                }

            }
        }

    }

    /**
     * 作者zheng的插入方法，未优化前，优化后参见上面insert()
     *
     * @param value
     * @param level 0 表示随机层数，不为0，表示指定层数，指定层数
     *              可以让每次打印结果不变动，这里是为了便于学习理解
     */
    public void insertPlanC(int value, int level) {
        // 随机一个层数
        if (level == 0) {
            level = randomLevel();
        }
        // 创建新节点
        Node newNode = new Node(level);
        newNode.data = value;
        // 表示从最大层到底层,都要有节点数据
        newNode.maxLevel = level;
        // 记录要更新的层数,表示新节点要更新到哪几层
        Node[] update = new Node[level];
        for (int i = 0; i < level; i++) {
            update[i] = head;
        }
        /**
         *
         * 1，说明：层是从下到上的，这里最下层编号是0，最上层编号是15
         * 2，这里没有从已有数据最大层（编号最大）开始找，（而是随机层的最大层）导致有些问题。
         *    如果数据量为1亿，随机level=1 ，那么插入时间复杂度为O（n）
         */
        Node p = head;
        for (int i = level - 1; i >= 0; i--) {
            while (null != p.forwards[i] && p.forwards[i].data < value) {
                p = p.forwards[i];
            }
            // 这里update[i]表示当前层节点的前一节点,因为要找到前一节点,才好插入数据
            update[i] = p;
        }
        // 将每一层节点和后面节点关联
        for (int i = 0; i < level; i++) {
            // 记录当前层节点后面节点指针
            newNode.forwards[i] = update[i].forwards[i];
            // 前一个及诶单的指针,指向当前节点
            update[i].forwards[i] = newNode;
        }
        // 更新层高
        if (levelCount < level) {
            levelCount = level;
        }
    }

    public void delete(int value) {
        Node[] update = new Node[levelCount];
        Node p = head;
        for (int i = levelCount - 1; i >= 0; i--) {
            while (null != p.forwards[i] && p.forwards[i].data < value) {
                p = p.forwards[i];
            }
            update[i] = p;
        }
        if (null != p.forwards[0] && p.forwards[0].data == value) {
            for (int i = levelCount - 1; i >= 0; i--) {
                if (null != update[i].forwards[i] && update[i].forwards[i].data == value) {
                    update[i].forwards[i] = update[i].forwards[i].forwards[i];
                }
            }
        }
    }

    /**
     * 打印每个节点数据和最大层数
     */
    public void printAll() {
        Node p = head;
        while (p.forwards[0] != null) {
            System.out.print(p.forwards[0] + " ");
            p = p.forwards[0];
        }
        System.out.println();
    }

    /**
     * 打印所有数据
     */
    public void printAll_beautiful() {
        Node p = head;
        Node[] c = p.forwards;
        Node[] d = c;
        int maxLevel = c.length;
        for (int i = maxLevel - 1; i >= 0; i--) {
            do {
                System.out.print((d[i] != null ? d[i].data : null) + ":" + i + "-------");
            } while (d[i] != null && (d = d[i].forwards)[i] != null);
            System.out.println();
            d = c;
        }
    }

    /**
     * 随机 level 次，如果是奇数层数 +1，防止伪随机
     *
     * @return
     */
    private int randomLevel() {
        int level = 1;
        for (int i = 1; i < MAX_LEVEL; i++) {
            if (random.nextInt() % 2 == 1) {
                level++;
            }
        }
        return level;
    }


    /**
     * 跳表的节点，每个节点记录了当前节点数据和所在层数数据
     */
    public class Node {
        private int data = -1;

        /**
         * 表示当前节点位置的下一个节点所层的数据,
         * 从上层切换到下层,就是数组下标-1
         * forwards[3]表示当前节点在第三层的下一个节点
         */
        private Node[] forwards;

        /**
         * 这个值其实可以不用，看优化insert()
         */
        private int maxLevel = 0;

        public Node(int level) {
            forwards = new Node[level];
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{ data: ");
            builder.append(data);
            builder.append("; levels: ");
            builder.append(maxLevel);
            builder.append(" }");
            return builder.toString();
        }
    }

    public static void main(String[] args) {
        SkipListOptimize list = new SkipListOptimize();
        list.insertPlanC(1, 3);
        list.insertPlanC(2, 3);
        list.insertPlanC(3, 2);
        list.insertPlanC(4, 4);
        list.insertPlanC(5, 10);
        list.insertPlanC(6, 4);
        list.insertPlanC(8, 5);
        list.insertPlanC(7, 4);
        list.printAll_beautiful();
        list.printAll();

        // 优化后insert()

        SkipListOptimize list2 = new SkipListOptimize();
        list2.insertPlanB(1);
        list2.insertPlanB(2);
        list2.insertPlanB(6);
        list2.insertPlanB(7);
        list2.insertPlanB(8);
        list2.insertPlanB(3);
        list2.insertPlanB(4);
        list2.insertPlanB(5);
        System.out.println();
        list2.printAll_beautiful();
        list2.printAll();


    }
}
