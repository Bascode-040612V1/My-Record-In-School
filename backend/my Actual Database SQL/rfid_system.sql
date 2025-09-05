-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 02, 2025 at 03:07 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `rfid_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`id`, `username`, `password`) VALUES
(2, 'ajJ', '12345678'),
(3, 'Guard', '12345678');

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `time_in` datetime DEFAULT NULL,
  `time_out` datetime DEFAULT NULL,
  `rfid` varchar(255) DEFAULT NULL,
  `date` date NOT NULL DEFAULT curdate()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `attendance`
--

INSERT INTO `attendance` (`id`, `student_id`, `time_in`, `time_out`, `rfid`, `date`) VALUES
(163, 29, '2025-07-15 10:39:25', NULL, NULL, '2025-07-15'),
(164, 26, '2025-07-15 10:39:37', NULL, NULL, '2025-07-15');

-- --------------------------------------------------------

--
-- Table structure for table `rfid_scans`
--

CREATE TABLE `rfid_scans` (
  `id` int(11) NOT NULL,
  `rfid_number` varchar(50) NOT NULL,
  `scanned_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rfid_scans`
--

INSERT INTO `rfid_scans` (`id`, `rfid_number`, `scanned_at`) VALUES
(1, '04A3F25C89', '2025-09-02 12:09:44'),
(2, '04A3F25C80', '2025-09-02 12:10:37'),
(5, '12345678p', '2025-09-02 13:00:02'),
(6, '3456789', '2025-09-02 13:01:39');

-- --------------------------------------------------------

--
-- Table structure for table `saved_attendance`
--

CREATE TABLE `saved_attendance` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `time_in` datetime NOT NULL,
  `time_out` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `student_number` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `saved_time_in` datetime DEFAULT NULL,
  `saved_time_out` datetime DEFAULT NULL,
  `saved_date` date DEFAULT NULL,
  `present_days` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `saved_attendance`
--

INSERT INTO `saved_attendance` (`id`, `student_id`, `time_in`, `time_out`, `name`, `student_number`, `image`, `saved_time_in`, `saved_time_out`, `saved_date`, `present_days`) VALUES
(7, 9, '0000-00-00 00:00:00', NULL, 'Jasper Andam', '220000', 'uploads/jasper.jpg', '2025-04-29 17:45:00', '2025-04-29 17:45:32', '2025-04-29', 0),
(8, 19, '0000-00-00 00:00:00', NULL, 'Lester Sam Duremdes', '220312', 'uploads/680868ca60db6.jpg', '2025-04-29 17:45:02', '2025-04-29 17:45:31', '2025-04-29', 0),
(9, 29, '0000-00-00 00:00:00', NULL, 'Jang Won-young', '220143', 'uploads/6810814143318.jpg', '2025-04-29 17:45:04', '2025-04-29 17:45:29', '2025-04-29', 0),
(10, 14, '0000-00-00 00:00:00', NULL, 'Nashria Macalatas', '220353', 'uploads/nash.jpg', '2025-04-29 17:45:06', '2025-04-29 17:45:26', '2025-04-29', 0),
(11, 26, '0000-00-00 00:00:00', NULL, 'Oliver Burro', '220043', 'uploads/6808aedcb7671.jpg', '2025-04-29 17:45:09', '2025-04-29 17:45:24', '2025-04-29', 0),
(12, 4, '0000-00-00 00:00:00', NULL, 'John Lloyd Figuracion', '220062', 'uploads/6806f31ae480a.jpg', '2025-04-29 17:45:11', '2025-04-29 17:45:18', '2025-04-29', 0),
(13, 9, '0000-00-00 00:00:00', NULL, 'Jasper Andam', '220000', 'uploads/jasper.jpg', '2025-04-30 20:34:49', '2025-04-30 20:35:10', '2025-04-30', 0),
(14, 19, '0000-00-00 00:00:00', NULL, 'Lester Sam Duremdes', '220312', 'uploads/680868ca60db6.jpg', '2025-04-30 20:34:51', '2025-04-30 20:35:15', '2025-04-30', 0),
(15, 29, '0000-00-00 00:00:00', NULL, 'Jang Won-young', '220143', 'uploads/6810814143318.jpg', '2025-04-30 20:34:52', '2025-04-30 20:35:12', '2025-04-30', 0),
(16, 14, '0000-00-00 00:00:00', NULL, 'Nashria Macalatas', '220353', 'uploads/nash.jpg', '2025-04-30 20:34:56', '2025-04-30 20:35:08', '2025-04-30', 0),
(17, 26, '0000-00-00 00:00:00', NULL, 'Oliver Burro', '220043', 'uploads/6808aedcb7671.jpg', '2025-04-30 20:34:57', '2025-04-30 20:35:03', '2025-04-30', 0),
(26, 26, '0000-00-00 00:00:00', NULL, 'Oliver Burro', '220043', 'uploads/6808aedcb7671.jpg', '2025-05-01 17:25:28', '2025-05-01 17:25:40', '2025-05-01', 0),
(27, 14, '0000-00-00 00:00:00', NULL, 'Nashria Macalatas', '220353', 'uploads/Screenshot 2025-05-01 142136.png', '2025-05-01 17:25:30', '2025-05-01 17:25:41', '2025-05-01', 0),
(28, 19, '0000-00-00 00:00:00', NULL, 'Mike Kevin Obumani', '220123', 'uploads/Screenshot 2025-05-01 141859.png', '2025-05-01 17:25:32', '2025-05-01 17:25:39', '2025-05-01', 0),
(29, 29, '0000-00-00 00:00:00', NULL, 'Jang Won-young', '220143', 'uploads/6810814143318.jpg', '2025-05-02 09:36:41', '2025-05-02 09:42:03', '2025-05-02', 0),
(30, 26, '0000-00-00 00:00:00', NULL, 'Oliver Burro', '220043', 'uploads/6808aedcb7671.jpg', '2025-05-02 09:37:57', '2025-05-02 09:42:07', '2025-05-02', 0),
(31, 9, '0000-00-00 00:00:00', NULL, 'Jasper Andam', '220000', 'uploads/jasper.jpg', '2025-05-02 09:37:59', '2025-05-02 09:42:11', '2025-05-02', 0),
(32, 14, '0000-00-00 00:00:00', NULL, 'Nashria Macalatas', '220353', 'uploads/Screenshot 2025-05-01 142136.png', '2025-05-02 09:38:01', '2025-05-02 09:42:09', '2025-05-02', 0),
(33, 4, '0000-00-00 00:00:00', NULL, 'John Lloyd Figuracion', '220062', 'uploads/6806f31ae480a.jpg', '2025-05-02 09:38:03', '2025-05-02 09:42:13', '2025-05-02', 0),
(34, 29, '0000-00-00 00:00:00', NULL, 'Jang Won-young', '220143', 'uploads/6810814143318.jpg', '2025-05-17 01:25:26', '2025-05-17 01:28:38', '2025-05-17', 0),
(35, 9, '0000-00-00 00:00:00', NULL, 'Jasper Andam', '220000', 'uploads/jasper.jpg', '2025-05-17 01:25:45', '2025-05-17 01:28:41', '2025-05-17', 0),
(36, 14, '0000-00-00 00:00:00', NULL, 'Nashria Macalatas', '220353', 'uploads/Screenshot 2025-05-01 142136.png', '2025-05-17 01:25:50', '2025-05-17 01:28:42', '2025-05-17', 0),
(37, 19, '0000-00-00 00:00:00', NULL, 'Mike Kevin Obumani', '220123', 'uploads/Screenshot 2025-05-01 141859.png', '2025-05-17 01:25:53', '2025-05-17 01:28:44', '2025-05-17', 0),
(38, 26, '0000-00-00 00:00:00', NULL, 'Oliver Burro', '220043', 'uploads/6808aedcb7671.jpg', '2025-05-17 01:25:55', '2025-05-17 01:28:45', '2025-05-17', 0);

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `student_number` varchar(50) NOT NULL,
  `rfid` varchar(50) NOT NULL,
  `image` varchar(255) DEFAULT 'assets/default-profile.png'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `name`, `student_number`, `rfid`, `image`) VALUES
(4, 'John Lloyd Figuracion', '220062', '3870395556', 'uploads/6806f31ae480a.jpg'),
(9, 'Jasper Andam', '220000', '3870909764', 'uploads/jasper.jpg'),
(14, 'Nashria Macalatas', '220353', '3871664244', 'uploads/Screenshot 2025-05-01 142136.png'),
(19, 'Mike Kevin Obumani', '220123', '3870456948', 'uploads/Screenshot 2025-05-01 141859.png'),
(26, 'Oliver Burro', '220043', '3870628244', 'uploads/6808aedcb7671.jpg'),
(29, 'Jang Won-young', '220143', '3870258948', 'uploads/6810814143318.jpg'),
(32, 'Joshua P. Bascode', '2203421', '', 'assets/default-profile.png'),
(33, 'Joshua P. Basco', '220342', '23456789098765', 'assets/default-profile.png');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `rfid_scans`
--
ALTER TABLE `rfid_scans`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `saved_attendance`
--
ALTER TABLE `saved_attendance`
  ADD PRIMARY KEY (`id`),
  ADD KEY `student_id` (`student_id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `student_number` (`student_number`),
  ADD UNIQUE KEY `rfid` (`rfid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=165;

--
-- AUTO_INCREMENT for table `rfid_scans`
--
ALTER TABLE `rfid_scans`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `saved_attendance`
--
ALTER TABLE `saved_attendance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `attendance`
--
ALTER TABLE `attendance`
  ADD CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `saved_attendance`
--
ALTER TABLE `saved_attendance`
  ADD CONSTRAINT `saved_attendance_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
