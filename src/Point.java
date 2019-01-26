public class Point {
        // 分区节点类
        private int size;   // 分区大小
        private int head;   // 分区的始址

        public Point(int head, int size) {
            this.head = head;
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getHead() {
            return head;
        }

        public void setHead(int head) {
            this.head = head;
        }
}
